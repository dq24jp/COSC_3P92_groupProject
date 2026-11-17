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
- RESET: resets the emulator
- 