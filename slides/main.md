---
marp: true
theme: default
paginate: true
style: |
  section {
    font-family: 'Helvetica Neue', Arial, sans-serif;
  }
  section.title {
    text-align: center;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  section.agenda td {
    font-size: 0.7em;
  }
  a {
    color: #1a6fb5;
  }
  .columns {
    display: flex;
    gap: 2em;
  }
  .columns > div {
    flex: 1;
  }
---

<!-- _class: title -->
<!-- _paginate: false -->

# SNNs to Silicon

### From Spiking Neural Networks to FPGA Hardware via NIR

**ISCAS 2026 Tutorial**

Michail Rontionov, Jens E. Pedersen, Charlotte Frenkel,
Francisco Ayala Le Brun, Nassim Beladel

---

## Why this tutorial?

- **Neuromorphic computing** with spiking neural networks (SNNs) offers lower latency and power for edge AI
- The ecosystem is **fragmented**: models are tied to specific simulators, toolchains, and hardware
- The **simulation-to-hardware gap** introduces subtle numerical divergences that are hard to localize

### Our approach

Use the **Neuromorphic Intermediate Representation** ([NIR](https://neuroir.org)) as a shared specification to go from trained SNNs to FPGA bitstreams -- with verification at every step.

```
P[Continuous] --Simulate--> P[Discrete] --Quantize--> P[Quantized] --Compile--> P[Hardware]
```

---

<!-- _class: organizers -->

## Organizers

<div class="columns">
<div>

**Michail Rontionov**
University of Southampton
NIR2FPGA, hands-on exercises

**Jens Egholm Pedersen**
Technical University of Denmark
NIR, SNNs in JAX

**Charlotte Frenkel**
Delft University of Technology
NC on FPGA & ASIC

</div>
<div>

**Francisco Ayala Le Brun**
Independent Researcher
SpinalHDL

**Nassim Beladel**
ETH Zurich
Hands-on tutorial support

</div>
</div>

---

<!-- _class: agenda -->

## Agenda

| Time | Topic | Presenter(s) |
|------|-------|---------------|
| 08:30 | Welcome | Michail & Jens |
| 08:40 | NC introduction and design approaches | Charlotte |
| 09:00 | NC on FPGA & ASIC | Michail & Nassim |
| 09:20 | NIR | Jens |
| 09:40 | SNNs in JAX | Jens |
| 09:50 | Hands-on: SNNs | Jens & Michail |
| *10:10* | *Break* | |
| 10:30 | NIR2FPGA | Michail |
| 10:45 | SpinalHDL | Francisco |
| 11:00 | Hands-on: RTL & Simulation | Michail, Jens & Nassim |
| 11:55 | Goodbye | Michail & Jens |

---

<!-- _class: title -->

## NC Introduction and Design Approaches

**Charlotte Frenkel** -- Delft University of Technology

*08:40 - 09:00*

---

<!-- _class: title -->

## NC on FPGA & ASIC

**Michail Rontionov & Nassim Beladel**

*09:00 - 09:20*

---

<!-- _class: title -->

## Neuromorphic Intermediate Representation (NIR)

**Jens E. Pedersen** -- Technical University of Denmark

*09:20 - 09:40*

---

<!-- _class: title -->

## SNNs in JAX

**Jens E. Pedersen** -- Technical University of Denmark

*09:40 - 09:50*

---

<!-- _class: title -->

## Hands-on: SNNs

**Jens E. Pedersen & Michail Rontionov**

*09:50 - 10:10*

---

<!-- _class: title -->

## Break

*10:10 - 10:30*

---

<!-- _class: title -->

## NIR2FPGA

**Michail Rontionov** -- University of Southampton

*10:30 - 10:45*

---

<!-- _class: title -->

## SpinalHDL

**Francisco Ayala Le Brun**

*10:45 - 11:00*

---

<!-- _class: title -->

## Hands-on: RTL & Simulation

**Michail Rontionov, Jens E. Pedersen & Nassim Beladel**

*11:00 - 11:55*

---

## Summary & Thank you

**What we covered today:**

1. **Neuromorphic computing** -- co-design, FPGA & ASIC landscape
2. **NIR** -- a shared, platform-independent graph representation for SNNs
3. **SNNs in JAX** -- training and exporting spiking networks
4. **NIR2FPGA** -- from NIR graphs to streaming FPGA accelerators via SpinalHDL

**Key takeaway: NIR can drive both simulation and synthesis**

**Code:** [github.com/mrontio/nir2fpga](https://github.com/mrontio/nir2fpga/) -- **NIR:** [neuroir.org](https://neuroir.org)

**Contact:** Michail (*m.rontionov@soton.ac.uk*) and Jens (*jegpe@dtu.dk*)
