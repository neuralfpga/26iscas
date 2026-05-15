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

  val memConfig = Activations.Config(c.quants("v_mem"), c.input.shape, c.input.width)
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

  val rFactor = AF(c.r, c.quants("v_mem").qformat)


  // Perform integration
  val integrated = memReadWithPayload.map { p =>
    val state   = p.value         // v[t - 1]
    val input = p.linked        // input[t]

    val integratedState =
      Vec(state.zip(input.fragment.value).map { case (v, inp) =>
        //  v[t - 1] + (r * input[t])
        (v + (rFactor * inp)
          // Handle Quantization
          .fixTo(c.quants("v_mem").qformat, RoundType.FLOOR))
          .fixTo(c.quants("v_mem").qformat, RoundType.FLOOR)
      })

    TupleBundle(integratedState, input) // v[t], input packet
  }

  val alpha = c.tau match {
    case Some(tau) => scala.math.exp(-(1 / tau))
    case None      => 1.0
  }
  val alphaFactor = Vec(AF(alpha, c.quants("v_mem").qformat), c.input.width)

  val leaked = integrated.map { p =>
    val integratedState = p._1 // v[t]
    val input    = p._2 // input packet

    val leakedState = Vec(integratedState
      .zip(alphaFactor)
      .map { case (iState, alpha) =>
      // α * v[t]
        (alpha * iState)
          // Handle Quantization
          .fixTo(c.quants("v_mem").qformat, RoundType.FLOOR)
    })

    TupleBundle(leakedState, input)
  }


  // Write updated membrane back
  mem.write(
    address = leaked.payload._2.fragment.flattenedAddress,
    data    = leaked.payload._1,
    enable  = leaked.fire
  )

  // --- Drive output ---
  leaked.translateInto(output) { case (out, from) =>
    val state   = from._1
    val input = from._2
    out.last            := input.last
    out.fragment.coords := input.fragment.coords
    out.fragment.value  := Vec(
      state.map(_.fixTo(c.quants("output").qformat, RoundType.FLOOR))
    )
  }
}

object Neuron {

  case class Config(
    input: Activations.Config,
    r: Double,
    tau: Option[Double],
    resetValue: Double,
    threshold: Option[Double],
    dt: Double,
    quants: Map[String, QuantizationConfig],
    timesteps: Option[Int] = None
  )

}
