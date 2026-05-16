package NIR2FPGA.Primitives.Implementations

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import NIR2FPGA.Activations
import NIR2FPGA.QuantizationConfig

case class Neuron(c: Neuron.Config) extends Component {
  val input  = slave  Stream(Fragment(Activations(c.input)))
  val outputConfig = Activations.Config(c.quants("output"), c.input.shape, c.input.width)
  val output = master Stream(Fragment(Activations(outputConfig))).simPublic()

  val mem = Mem(Vec(AFix(c.quants("v_mem").qformat), c.input.width), c.input.linearSize)
    .initBigInt(List.fill(c.input.linearSize)(BigInt(0)))

  // Simulation probe for v_mem
  val debug_mem_probe = Vec.fill(scala.math.min(c.input.linearSize, 4))(
    Vec(AFix(c.quants("v_mem").qformat), c.input.width)
  )
  debug_mem_probe.simPublic()
  debug_mem_probe.setName("v_mem")

  for (i <- debug_mem_probe.indices)
    debug_mem_probe(i) := mem.readAsync(U(i, log2Up(c.input.linearSize) bits))

  val addr = input.payload.fragment.flattenedAddress
  val memReadWithPayload = mem.streamReadSync(
    input.translateWith(addr),
    input.payload
  )

  val isLIF = c.v_threshold.isDefined && c.v_reset.isDefined

  val alpha = c.tau match {
    case Some(tau) => 1.0 - 1.0 / tau
    case None      => 1.0
  }

  val inputScale = if (isLIF) c.r * (1.0 - alpha) else c.r
  val rFactor    = AF(inputScale, c.quants("v_mem").qformat)

  val alphaFactor = Vec(AF(alpha, c.quants("v_mem").qformat), c.input.width)

  // Leak: α * v[t-1]
  val leaked = memReadWithPayload.map { p =>
    val state = p.value
    val input = p.linked

    val leakedState = Vec(state
      .zip(alphaFactor)
      .map { case (v, alpha) =>
        (alpha * v)
          .fixTo(c.quants("v_mem").qformat, RoundType.CEIL)
      })

    TupleBundle(leakedState, input)
  }

  // Integrate: leaked + (1-α)*r*input
  val integrated = leaked.map { p =>
    val leakedState = p._1
    val input       = p._2

    val integratedState = Vec(leakedState.zip(input.fragment.value).map { case (v, inp) =>
      (v + (rFactor * inp)
        .fixTo(c.quants("v_mem").qformat, RoundType.CEIL))
        .fixTo(c.quants("v_mem").qformat, RoundType.CEIL)
    })

    TupleBundle(integratedState, input)
  }


  val afterFiring = (c.v_threshold, c.v_reset) match {
    case (Some(vth), Some(vreset)) =>
      integrated.map { p =>
        val integratedState = p._1
        val input           = p._2

        val vthFactor = AF(vth, c.quants("v_mem").qformat)
        val fired     = Vec(integratedState.map(_ >= vthFactor))

        val nextState = Vec(integratedState.zip(fired).map { case (v, f) =>
          Mux(f, AF(0.0, c.quants("v_mem").qformat), v)
        })

        TupleBundle(nextState, input, fired)
      }
    case _ =>
      integrated.map { p => TupleBundle(p._1, p._2, Vec(Seq.fill(c.input.width)(False))) }
  }

  // Write updated membrane back
  mem.write(
    address = afterFiring.payload._2.fragment.flattenedAddress,
    data    = afterFiring.payload._1,
    enable  = afterFiring.fire
  )

  // --- Drive output ---
  afterFiring.translateInto(output) { case (out, from) =>
    val state  = from._1
    val input  = from._2
    val spikes = from._3
    out.last            := input.last
    out.fragment.coords := input.fragment.coords
    out.fragment.value  := Vec(
      if (isLIF)
        spikes.map(s => Mux(s, AF(1.0, c.quants("output").qformat), AF(0.0, c.quants("output").qformat)))
      else
        state.map(_.fixTo(c.quants("output").qformat, RoundType.FLOOR))
    )
  }
}

object Neuron {

  case class Config(
    input: Activations.Config,
    r: Double,
    tau: Option[Double],
    v_reset: Option[Double],
    v_threshold: Option[Double],
    dt: Double,
    quants: Map[String, QuantizationConfig],
    timesteps: Option[Int] = None
  )

}
