package NIR2FPGA.Primitives

import spinal.core._
import spinal.lib._
import nir._

import NIR2FPGA.ConfigJSON
import NIR2FPGA.Activations
import NIR2FPGA.Primitives.Implementations.Neuron

final case class LIFHW(
  id: String,
  params: LIFParams,
  config: ConfigJSON
) extends PrimitiveHW[LIFParams] {

  /* α = exp ^ (-1 / tau) */
  /* v[t] = α · (v[t-1] + input[t]) */

  def makeHardware(inputAct: Activations): (Stream[Fragment[Activations]], Stream[Fragment[Activations]]) = {
    val dt           = 1.0 // Discrete timestep = 1 time unit
    val maxTimesteps = config.timesteps + 1

    // For now, assert that v_leak is zero
    val vLeakValue = NodeHelper.extractScalar(params.v_leak)
    require(vLeakValue == 0.0, s"LIFParams: v_leak must be zero for now, got ${vLeakValue}")

    val v_threshold = NodeHelper.extractScalar(params.v_threshold)
    val v_reset = NodeHelper.extractScalar(params.v_reset)

    val lifconfig = Neuron.Config(
      input = inputAct.c,
      tau =  Some(NodeHelper.extractScalar(params.tau)),
      r = NodeHelper.extractScalar(params.r),
      v_reset = Some(v_reset),
      v_threshold = Some(v_threshold),
      dt = dt,
      quants = config.quantizations(id),
      timesteps = Some(config.timesteps)
    )

    val neuron = Neuron(lifconfig).setName("lif")
    (neuron.input, neuron.output)
  }

}
