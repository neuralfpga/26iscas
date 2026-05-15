package NIR2FPGA.Primitives

/* Scala Imports */
import spinal.core._
import spinal.lib._
import nir._
import nir.tensor.Tensor

/* NIR2FPGA Imports */
import NIR2FPGA.Activations
import NIR2FPGA.AcceleratorConfig
import NIR2FPGA.ConfigJSON

/* Define the PrimitiveHW Trait:
 - We use traits to "mark" objects that implement functionality.
 - To implement a primitive in hardware, we need to define
   1. params variable -- Which NIR primitive are we implementing?
   2. makeHardware function -- how to map the primitive parameters to the hardware implementation.
 */
trait PrimitiveHW[P <: NIRParams] {
  val params: P
  def makeHardware(inputAct: Activations): (Stream[Fragment[Activations]], Stream[Fragment[Activations]])
}

object PrimitiveHW {
  def create(id: String, params: NIRParams, config: ConfigJSON, accelConfig: AcceleratorConfig): PrimitiveHW[_] =
    params match {
      case p: IParams     =>  IHW(id, p, config)
      case p: LIParams     =>  LIHW(id, p, config)
      case _ => throw new Exception(f"Not yet supported: ${params.getClass()}")
    }
}

object NodeHelper {

  def extractTensorValues(tensor: Tensor[Float]): List[Double] =
    tensor.map(_.toDouble).toFlatList

  def extractScalar(tensor: Tensor[Float]): Double = {
    val values = extractTensorValues(tensor)
    require(values.nonEmpty, s"Tensor must contain at least one value")
    values.head
  }

  def extractLongTensorValues(tensor: Tensor[Long]): List[Int] =
    tensor.map(_.toInt).toFlatList

}
