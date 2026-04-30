---
theme: default
title: "Hands-on: Defining, Training & Exporting SNNs"
info: |
  ISCAS 2026 Tutorial — SNNs to Silicon
class: text-center
drawings:
  persist: false
transition: slide-left
mdc: true
math: katex
---

# Hands-on: Defining, Training & Exporting SNNs

### From dynamical systems to hardware-ready models

<br/>

**Jens E. Pedersen** & **Michail Rontionov**

<br/>

ISCAS 2026 Tutorial &mdash; SNNs to Silicon

---

# The leaky integrate-and-fire neuron

<div class="grid grid-cols-2 gap-8 items-center">
<div>

The canonical spiking neuron is a **leaky bucket**:

$$\tau \frac{dv}{dt} = -v + I(t)$$

$$\text{if } v \geq \theta: \text{ emit spike, } v \leftarrow 0$$

<v-clicks>

- Input $I$ charges the membrane potential $v$
- $v$ **leaks** towards zero with time constant $\tau$
- When $v$ crosses threshold $\theta$ &rarr; **spike** and reset
- This ODE is what NIR encodes &mdash; and what hardware implements

</v-clicks>

</div>
<div>

<div style="height: 350px; overflow: hidden;">
<iframe src="lif-sim.html" class="border-0" style="width: 100%; height: 700px; transform: scale(0.85); transform-origin: top left;"></iframe>
</div>
<p class="text-xs text-gray-400 text-center">Interactive LIF neuron (dynsim)</p>

</div>
</div>

---

# Why JAX?

<div class="grid grid-cols-2 gap-8 items-center">
<div>

JAX gives us **functional transforms** over Python+NumPy:

<v-clicks>

- `jit` &mdash; compile to XLA, run on GPU/TPU
- `vmap` &mdash; auto-vectorize over batches
- `grad` &mdash; automatic differentiation
- Composable: `jit(vmap(grad(f)))`

</v-clicks>

<v-click>

<div class="mt-4 p-3 bg-blue-50 rounded-lg text-sm">

Write the **math once**, get batched + differentiated + compiled code for free &mdash; ideal for simulating dynamical systems

</div>

</v-click>

</div>
<div>

<v-click>

```python
import jax
import jax.numpy as jnp

# A pure function — no hidden state
def lif_step(v, x, tau=10.0, threshold=1.0):
    dv = (-v + x) / tau
    v_new = v + dv
    spike = (v_new >= threshold).astype(float)
    v_new = v_new * (1.0 - spike)  # reset
    return v_new, spike

# JIT-compiled, batched, differentiable
fast_step = jax.jit(jax.vmap(lif_step))
```

</v-click>

</div>
</div>

---

# Norse: PyTorch SNNs + NIR

<div class="grid grid-cols-2 gap-8 items-center">
<div>

**Norse** provides a PyTorch-native SNN library with first-class NIR support:

<v-clicks>

- Drop-in spiking neuron modules (LIF, LI, IF, ...)
- Integrates with standard PyTorch training loops
- Direct **export to NIR** for hardware deployment
- Used in research and production

</v-clicks>

<v-click>

```python
import norse.torch as norse

model = norse.SequentialState(
    torch.nn.Linear(784, 128),
    norse.LIFCell(),
    torch.nn.Linear(128, 10),
    norse.LICell(),
)
```

</v-click>

</div>
<div>

<v-click>

**Export to NIR**:

```python
# After training...
nir_graph = norse.to_nir(model)

import nir
nir.write("trained_snn.nir", nir_graph)
```

</v-click>

<v-click>

<div class="mt-4 p-3 bg-purple-50 rounded-lg text-sm">

This NIR file is the input to **NIR2FPGA** &mdash; the same model goes from PyTorch to FPGA with no manual translation

</div>

</v-click>

</div>
</div>

---

# The full pipeline

<div class="mt-2">

```
  Train in Norse/JAX          Export to NIR            Compile & deploy
┌──────────────────┐      ┌──────────────────┐      ┌──────────────────┐
│  LIF dynamics    │      │ ODE primitives   │      │  NIR2FPGA → RTL  │
│  Surrogate grads │ ───► │ HDF5 format      │ ───► │  synfire.dev     │
│  GPU-accelerated │      │ Platform-agnostic│      │  Hardware chips  │
└──────────────────┘      └──────────────────┘      └──────────────────┘
```

</div>

<div class="grid grid-cols-3 gap-4 mt-4">
<div class="p-3 bg-blue-50 rounded-lg text-center text-sm" v-click>

**Norse / JAX**

Familiar frameworks, GPU training, surrogate gradients

</div>
<div class="p-3 bg-green-50 rounded-lg text-center text-sm" v-click>

**NIR**

One model format for all targets

</div>
<div class="p-3 bg-purple-50 rounded-lg text-center text-sm" v-click>

**FPGA**

Custom streaming dataflow &mdash; no wasted silicon

</div>
</div>

<v-click>

<div class="mt-4 p-3 bg-yellow-50 rounded-lg text-center">

**After the break**: Michail will show how NIR2FPGA compiles your NIR graph into SpinalHDL RTL

</div>

</v-click>

---

# Hands-on exercise

<div class="grid grid-cols-[3fr_2fr] gap-8 items-center">
<div>

**Your turn!** Open the notebook and:

<v-clicks>

1. Implement a LIF neuron in Norse/Jax
2. Simulate it on spike train inputs
3. Build a simple SNN (Linear &rarr; LIF &rarr; Linear &rarr; LI)
4. Train on MNIST with surrogate gradients
5. Export to NIR
6. Inspect the NIR graph

</v-clicks>

<v-click>

<div class="mt-4 p-3 bg-green-50 rounded-lg text-sm">

The NIR file you produce here will be the input for the **NIR2FPGA hands-on** after the break

</div>

</v-click>

</div>
<div>

<div class="p-4 bg-blue-50 rounded-lg text-center">

**Notebook link**

<br/>

*QR code / link here*

<br/>

</div>

<v-click>

<div class="mt-4 p-3 bg-orange-50 rounded-lg text-sm text-center">

**Want to learn more?**

Free SNN textbook: [snnbook.net](https://snnbook.net)

</div>

</v-click>

</div>
</div>
