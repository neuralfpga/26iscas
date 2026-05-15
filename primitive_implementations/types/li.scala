package NIR2FPGA.Primitives

import spinal.core._
import spinal.lib._
import nir._

import NIR2FPGA.ConfigJSON
import NIR2FPGA.Activations
import NIR2FPGA.Primitives.Implementations.Neuron

final case class LIHW(
  id: String,
  params: LIParams,
  config: ConfigJSON
) extends PrimitiveHW[LIParams] {

  def makeHardware(inputAct: Activations): (Stream[Fragment[Activations]], Stream[Fragment[Activations]]) = {
    val dt           = 1.0 // Discrete timestep = 1 time unit
    val maxTimesteps = config.timesteps + 1

    // For now, assert that v_leak is zero
    val vLeakValue = NodeHelper.extractScalar(params.v_leak)
    require(vLeakValue == 0.0, s"LIParams: v_leak must be zero for now, got ${vLeakValue}")

    val liconfig = Neuron.Config(
      input = inputAct.c,
      r = NodeHelper.extractScalar(params.r),
      resetValue = 0.0,
      threshold = None,
      dt = dt,
      quants = config.quantizations(id),
      timesteps = Some(config.timesteps)
    )

    val neuron = Neuron(liconfig).setName("li")
    (neuron.input, neuron.output)
  }

}
