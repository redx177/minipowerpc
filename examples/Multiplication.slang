; Check for 0 factors:
100 CLR R0				; AKKU = 0
102 SWDD R0,#510		; total result positiv = 0
104 SWDD R0,#506		; total low (tl) = 0
106 SWDD R0,#504		; total high (th) = 0;
108 LWDD R0,#500		; AKKU = f1
110	BZD #350			; if f1 == 0, goto 350
; Check for negative factors:
112 SLL					; AKKU = AKKU * 2
114 BCD #210			; if f1 is negativ, make positiv.
116 LWDD R0,#502		; AKKU = f2
118 SLL					; AKKU = AKKU * 2
120 BCD #250			; if f2 is negativ, make positiv.
122 BD #150				; goto Loop preparation
; Loop preparation
150 LWDD R1,#500		; R1 = f1
; Loop:
152 LWDD R0,#502		; AKKU = f2
154 BZD #350			; if f2 == 0, goto End
156 DEC					; f2 - 1
158 SWDD R0,#502		; f2 = f2 - 1
160 LWDD R0,#506		; AKKU = tl
162 ADD R1				; tl + R1
164 BCD #180			; if overflow, goto Overflow
166 SWDD R0,#506		; tl = tl + R1
168 BD #152				; goto Loop
; Overflow handling:
180 ADDD #-16384
182 ADDD #-16384
184 SWDD R0,#506		; tl = tl + R1
186 LWDD R0,#504		; AKKU = th
188 INC					; th + 1
190 SWDD R0,#504		; th = th + 1
192 CLR R0				; C = 0
194 BD #152				; goto Loop
; Make f1 positiv and save 1 into #510 so total is negative.
210 LWDD R0,#500		; AKKU = f1
212 DEC					; Make positiv
214 NOT
216 SWDD R0,#500		; f1 = abs(f1)
218 LWDD R0,#510		; Load #510 (overall total negative?)
220 INC
222 SWDD R0,#510
224 BD #116
; Make f2 positiv and toggle #510 
; to show if total is negativ or not.
250 LWDD R0,#502		; AKKU = f2
252 DEC					; Make positiv
254 NOT
256 SWDD R0,#502		; f2 = abs(f2)
258 LWDD R0,#510		; Load #510 (overall total negative?)
260 BZD #270			; Toggle #510.
262 DEC
264 SWDD R0,#510
266 BD #150				; goto Loop preparation
270 INC
272 BD #264				; goto 3 steps back 
; Finish program:
350 LWDD R0,#510		; Check if result should be negative
352	BZD #380			; If not, finish.
354 LWDD R0,#504		; Make th negative.
356 NOT
358 SWDD R0,#504
360 LWDD R0,#506		; Make tl negative.
362 NOT
364 INC
366 SWDD R0,#506
368 BD #380
380 END