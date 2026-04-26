# SNNs to Silicon: ISCAS 2026 Tutorial

Tutorial at [IEEE ISCAS 2026](https://2026.ieee-iscas.org/program/Tutorial.html) on deploying spiking neural networks to FPGA hardware using the [Neuromorphic Intermediate Representation (NIR)](https://neuroir.org).

## Why this tutorial?

Neuromorphic computing (NC) with spiking neural networks (SNNs) promises lower latency and power compared to conventional dense architectures, making it attractive for edge AI.
However, the field suffers from a fragmented ecosystem: individual works are tied to specific models, simulators, and toolchains, and the gap between software simulation and hardware realization introduces subtle numerical divergences that are hard to track down.

[NIR](https://neuroir.org) addresses this by formalizing SNN computations as platform-independent graphs of dynamical systems -- similar to what ONNX does for conventional ML. NIR is already supported by hardware platforms like BrainScaleS, Intel Loihi, SynSense Speck/Xylo, and SpiNNaker2, and by software frameworks like Sinabs and Norse.

In this tutorial, we show how to go from a trained SNN all the way to running hardware on an FPGA, using [NIR2FPGA](https://github.com/neuralfpga): a compilation framework that generates streaming dataflow accelerators from NIR graphs via [SpinalHDL](https://github.com/SpinalHDL/SpinalHDL).
The key idea is that by designing around NIR, we can shorten the representational distance between the model and the hardware, and verify behavioral consistency at every step.

## Schedule

| Time | Topic | Presenter(s) |
|------|-------|---------------|
| **08:30 - 09:20** | **Welcome & Introduction** | |
| 08:30 | Welcome | Michail Rontionov & Jens E. Pedersen |
| 08:40 | NC introduction and design approaches | Charlotte Frenkel |
| 09:00 | NC on FPGA & ASIC | Michail Rontionov & Nassim Beladel |
| **09:20 - 10:10** | **NIR & SNNs** | |
| 09:20 | NIR | Jens E. Pedersen |
| 09:40 | SNNs in JAX | Jens E. Pedersen |
| 09:50 | Hands-on (20 min) | Jens E. Pedersen & Michail Rontionov |
| **10:10 - 10:30** | **Break** | |
| **10:30 - 11:00** | **NIR2FPGA** | |
| 10:30 | NIR2FPGA | Michail Rontionov |
| 10:45 | SpinalHDL | Francisco Ayala Le Brun |
| **11:00 - 12:00** | **Hands-on: RTL & Simulation** | |
| 11:00 | Tutorial | Michail Rontionov & Jens E. Pedersen & Nassim Beladel |
| 11:55 | Goodbye | Michail Rontionov & Jens E. Pedersen |

## Organizers

- **[Michail Rontionov](https://github.com/mrontio)** (University of Southampton) -- NIR2FPGA, hands-on exercises
- **[Jens Egholm Pedersen](https://jepedersen.dk)** (Technical University of Denmark) -- NIR, SNNs in JAX
- **[Charlotte Frenkel](https://chfrenkel.github.io/)** (Delft University of Technology) -- NC on FPGA & ASIC
- **[Francisco Ayala Le Brun](https://github.com/fayalalebrun)** -- SpinalHDL
- **[Nassim Beladel](https://github.com/nassimbd)** (ETH Zurich) -- Hands-on tutorial support

## References

- **NIR2FPGA paper**: Rontionov et al., *Generating Dataflow Accelerators from the Neuromorphic Intermediate Representation*
- **NIR**: [neuroir.org](https://neuroir.org) -- Neuromorphic Intermediate Representation
- **Code**: [github.com/mrontio/nir2fpga](https://github.com/mrontio/nir2fpga/)
