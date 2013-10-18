100 LWDD R0,#202 	; Akku :=  Inhalt Speicher(202 + 203) = b
102 LWDD R1,#200 	; R1 :=  Inhalt Speicher(200 + 201)= a
104 ADD R1 			; Akku := Akku + R1 = s
106 SWDD R0,#204 	; Inhalt Speicher(204 + 205) := Akku = s
108 LWDD R0,#202 	; Akku :=  Inhalt Speicher(202 + 203)= b
110 ADDD #4 		; Akku := Akku + 4 = r
112 SWDD R0,#206 	; Inhalt Speicher(206 + 207) := Akku = r
114 LWDD R0,#204 	; Akku :=  Inhalt Speicher(204 + 205)= s
116 LWDD R1,#206 	; R1:=  Inhalt Speicher(206 + 207)= r
118 ADD R1 			; Akku := Akku + R1 = s
120 SWDD R0,#204 	; Inhalt Speicher(204 + 205) := Akku = s