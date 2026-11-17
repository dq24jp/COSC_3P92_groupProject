# how to use the UI

1. run UI.main function in UI.java
2. Enter commands (one per line):
- EXIT: exits the UI
- LOADRAM [path]: loads S19 file from [path] into the emulator as RAM
- LOADROM [path]: loads S19 file from [path] into the emulator as ROM
- STEP: steps one instruction at a time
- RUN: Runs until it reaches a NOP or WAI
- INSPECT [addr]: returns the value in address [addr]. May be decimal (123) or hex (xFE or 0xFE)
- FLAG [F]: query the state of a flag:
    - C: Carry flag
    - Z: Zero flag
    - S: Sign flag
    - O: Overflow flag
    - I: Interrupt flag
    - A: Auxiliary carry flag
- REG [R]: returns the contents of register [R]:
  - A: Accumulator A (8 bit)
  - B: Accumulator B (8 bit)
  - S: Status register (8 bit)
  - PC: Program counter (16 bit)
  - SP: Stack pointer (16 bit)
  - IDX: Index register (16 bit)
- RESET: resets the emulator



# demo s19 files

demo_ram.s19 and demo_rom.s19 together contain a program which starts at 0x0108, and adds together 0x10 and 0x15, putting the result in memory location 0x0108.

Commands to test the example s19s:
- LOADROM demo_rom.s19
- LOADRAM demo_ram.s19
- run
- inspect 0x0108

This outputs a result of 37 (0x25).

