---
theme: default
title: "Neuromorphic Intermediate Representation (NIR)"
info: |
  ISCAS 2026 Tutorial — SNNs to Silicon
class: text-center
drawings:
  persist: false
transition: slide-left
mdc: true
math: katex
---

# Neuromorphic Intermediate Representation (NIR)

### A common language for neuromorphic systems

<br/>

**Jens E. Pedersen** &middot; jegpe@dtu.dk

DTU Electro

<br/>

ISCAS 2026 Tutorial &mdash; SNNs to Silicon

---

# The problem: a fragmented ecosystem

<div class="grid grid-cols-2 gap-8 items-center">
<div>

Training an SNN today means **choosing a stack**:

- Framework: Norse, snnTorch, Lava-DL, Sinabs, Spyx, ...
- Hardware: Loihi, BrainScaleS, SpiNNaker, Speck, Xylo, FPGA, ...
- Each combination requires **rewriting** the model

<v-click>

This is the same problem conventional ML solved with **ONNX** &mdash; but neuromorphic models have **dynamics** that ONNX cannot capture.

</v-click>

</div>
<div>

<img src="/img/programming_nm.png" class="h-55 mx-auto"/>
<p class="text-xs text-gray-400 text-center">Pedersen et al., Nature Reviews Electrical Engineering, 2025</p>

</div>
</div>

---

# NIR: one model, many platforms

<div class="grid grid-cols-2 gap-8 items-center">
<div>

NIR captures SNN models as **graphs of continuous-time dynamical systems**

<v-clicks>

- Primitives are **parameterized ODEs** &mdash; no discretization baked in
- The graph is **substrate-agnostic**
- Each backend instantiates the dynamics:
  - Software &rarr; any ODE solver or Euler step
  - Digital HW &rarr; fixed-point discretization
  - Analog HW &rarr; physics implements the ODE
  - **FPGA** &rarr; streaming dataflow (NIR2FPGA)

</v-clicks>

</div>
<div>

<img src="/img/nir.png" class="h-50 mx-auto mb-4"/>

<img src="/img/nir_platforms.png" class="h-35 mx-auto"/>

</div>
</div>

---

# What's in a NIR graph?

<div class="grid grid-cols-2 gap-8 items-center">
<div>

**17 primitives** covering the neuromorphic toolbox:

| Category | Primitives |
|----------|-----------|
| **Neurons** | LIF, LI, IF, CubaLIF, CubaLI |
| **Synapses** | Linear, Affine, Conv1d, Conv2d |
| **Pooling** | SumPool2d, AvgPool2d |
| **Utility** | Scale, Delay, Flatten, Threshold |
| **I/O** | Input, Output |

</div>
<div>

<v-click>

The **LIF neuron** in NIR:

$$\tau \frac{dv}{dt} = -(v - v_\text{leak}) + R \cdot I$$

$$\text{if } v \geq \theta: \text{ spike, } v \leftarrow v_\text{reset}$$

Parameters: $\tau$, $R$, $v_\text{leak}$, $v_\text{reset}$, $\theta$

&rarr; Fully defines the dynamics, independent of timestep or solver

</v-click>

</div>
</div>

---

# Supported platforms

<div class="grid grid-cols-2 gap-8 mt-4">
<div>

**10 software frameworks**

- Norse (PyTorch)
- snnTorch (PyTorch)
- Sinabs (PyTorch / SynSense)
- Lava-DL (Intel)
- Rockpool (SynSense)
- Nengo
- Spyx (JAX)
- jaxsnn (BrainScaleS)
- hxtorch (BrainScaleS)
- Spiking Jelly

</div>
<div>

**5+ hardware targets**

- Intel Loihi
- SynSense Speck & Xylo
- BrainScaleS-2
- SpiNNaker2
- **FPGA via NIR2FPGA** &larr; this tutorial!

<v-click>


<img src="/img/nir_hw.png" class="h-40 mx-auto mt-2"/>

</v-click>

</div>
</div>

---

# NIR in practice: export and import

<div class="grid grid-cols-2 gap-8">
<div>

**Export** from any supported framework:

```python
import nir
import norse

# Train your model in Norse
net = norse.torch.SequentialState(...)
# ... training loop ...

# Export to NIR
nir_graph = norse.torch.to_nir(net)
nir.write("my_snn.nir", nir_graph)
```

</div>
<div>

**Import** into any target:

```python
import nir

# Load the NIR graph
graph = nir.read("my_snn.nir")

# Inspect the model
for name, node in graph.nodes.items():
    print(f"{name}: {type(node).__name__}")
    # e.g., "lif_0: LIF"
    # e.g., "linear_0: Affine"

# Deploy to hardware, FPGA, or simulator
```

</div>
</div>

<v-click>

<div class="mt-4 p-3 bg-green-50 rounded-lg text-center text-sm">

File format is **HDF5** &mdash; portable, self-describing, and language-agnostic

</div>

</v-click>

---

# Building a NIR graph from scratch

```python
import nir
import numpy as np

graph = nir.NIRGraph(
    nodes={
        "input":  nir.Input({"input": np.array([784])}),
        "linear": nir.Affine(weight=np.random.randn(128, 784),
                             bias=np.zeros(128)),
        "lif":    nir.LIF(tau=np.full(128, 20.0),
                          r=np.ones(128),
                          v_leak=np.zeros(128),
                          v_threshold=np.ones(128),
                          v_reset=np.zeros(128)),
        "out":    nir.Affine(weight=np.random.randn(10, 128),
                             bias=np.zeros(10)),
        "output": nir.Output({"output": np.array([10])}),
    },
    edges=[("input", "linear"), ("linear", "lif"),
           ("lif", "out"), ("out", "output")]
)
nir.write("mnist_snn.nir", graph)  # Ready for NIR2FPGA!
```

---

# [synfire.dev](https://synfire.dev): the neuromorphic model registry

An open platform for sharing and deploying SNN models

<div class="grid grid-cols-2 gap-8 items-center">
<div>


<v-clicks>

- **Model registry**: publish and discover SNNs
- **Built on NIR**: models are stored as graphs
- **Hardware-aware deployment**: deploy to neuromorphic chips and FPGAs
- **CLI + SDK + Web**: multiple developer interfaces

</v-clicks>

<v-click>

<div class="mt-4 p-3 bg-purple-50 rounded-lg text-sm">

Think of it as **HuggingFace for neuromorphic AI** &mdash; train once, deploy everywhere

</div>

</v-click>

</div>
<div>

<v-click>

**Workflow**:

1. Train SNN in your preferred framework
2. Export to NIR
3. Upload to synfire.dev
4. Others discover & deploy your model
5. Target any supported hardware

</v-click>

</div>
</div>

---

# The NIR2FPGA pipeline

<div class="grid grid-cols-[2fr_3fr] gap-8 items-center">
<div>

This tutorial's end-to-end flow:

<v-clicks>

1. **Define** an SNN (JAX &mdash; next session)
2. **Train** with surrogate gradients
3. **Export** to NIR
4. **Compile** NIR &rarr; SpinalHDL RTL
5. **Simulate** or synthesize for FPGA

</v-clicks>

<v-click>

<div class="mt-4 p-3 bg-yellow-50 rounded-lg text-sm">

NIR closes the gap between **software simulation** and **hardware realization** &mdash; same model, verified at every step

</div>

</v-click>

</div>
<div>

```
┌─────────────┐
│  Norse/Jax  │  Define & train SNN
└──────┬──────┘
       │ export
       ▼
┌─────────────┐
│     NIR     │  Platform-independent graph
└──────┬──────┘
       │ compile
       ▼
┌─────────────┐
│  NIR2FPGA   │  Generate SpinalHDL
└──────┬──────┘
       │ synthesize
       ▼
┌─────────────┐
│    FPGA     │  Streaming dataflow accelerator
└─────────────┘
```

</div>
</div>

---

# Summary & resources

<div class="grid grid-cols-2 gap-8 mt-4">
<div>

**NIR** is the common language for neuromorphic systems:

- 17 ODE-based primitives
- 10 frameworks, 5 (now 6!) hardware targets
- HDF5 file format, open source
- **synfire.dev** for model sharing
- **NIR2FPGA** for FPGA deployment

</div>
<div>

**References & links**

- [neuroir.org](https://neuroir.org) &mdash; NIR documentation
- [synfire.dev](https://synfire.dev) &mdash; model registry
- [github.com/neuromorphs/NIR](https://github.com/neuromorphs/NIR)
- Paper: [Neuromorphic Intermediate Representation](https://www.nature.com/articles/s41467-024-52259-9), Nat. Commun, 2025
- Paper: [Neuromorphic Programming](https://arxiv.org/html/2410.22352v1), ICONS, 2024
- [snnbook.net](https://snnbook.net) &mdash; free SNN textbook

</div>
</div>

<br/>

<div class="text-center">

**Next up**: Defining, training & exporting SNNs &rarr;

</div>
