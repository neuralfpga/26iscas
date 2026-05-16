package NIR2FPGA.Primitives

import spinal.core._
import spinal.lib._
import nir._

import NIR2FPGA.ConfigJSON
import NIR2FPGA.Activations
import NIR2FPGA.Primitives.Implementations.Neuron

final case class IHW(
  id: String,
  params: IParams,
  config: ConfigJSON
) extends PrimitiveHW[IParams] {

  def makeHardware(inputAct: Activations): (Stream[Fragment[Activations]], Stream[Fragment[Activations]]) = {
    val dt           = 1.0 // Discrete timestep = 1 time unit
    val maxTimesteps = config.timesteps + 1

    val iconfig = Neuron.Config(
      input = inputAct.c,
      tau = None,
      r = NodeHelper.extractScalar(params.r),
      v_reset = None,
      v_threshold = None,
      dt = dt,
      quants = config.quantizations(id),
      timesteps = Some(config.timesteps)
    )

    val neuron = Neuron(iconfig).setName("i")
    (neuron.input, neuron.output)
  }

}
