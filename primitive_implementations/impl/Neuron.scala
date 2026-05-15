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

  val computed = memReadWithPayload.map { p =>
    val state   = p.value         // Vec[AFix] — current v_mem
    val payload = p.linked        // Fragment[Activations] — input

    val newState = Vec(
      state.zip(payload.fragment.value).map { case (v, inp) =>
        (v + (rFactor * inp).fixTo(c.quants("v_mem").qformat, RoundType.FLOOR))
          .fixTo(c.quants("v_mem").qformat, RoundType.FLOOR)
      }
    )

    TupleBundle(newState, payload)
  }

  // Write updated membrane back
  mem.write(
    address = computed.payload._2.fragment.flattenedAddress,
    data    = computed.payload._1,
    enable  = computed.fire
  )

  // --- Drive output ---
  computed.translateInto(output) { case (out, from) =>
    val state   = from._1
    val payload = from._2
    out.last            := payload.last
    out.fragment.coords := payload.fragment.coords
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
