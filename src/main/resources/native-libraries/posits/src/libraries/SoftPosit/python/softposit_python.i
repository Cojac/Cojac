/*
Author: S.H. Leong (Cerlane)

Copyright (c) 2018 Next Generation Arithmetic

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/


%module softposit
%{
#include "softposit.h"
%}


%include "softposit_types.h"
%include "softposit.h"
%include <stdint.i>

%pythonbegin %{
from __future__ import absolute_import, print_function, division
%}


%pythoncode %{

def convertToColor(i, ps, es):
   orig="{0:b}".format(i).zfill(ps)
   m=0
   regime=1
   exponent=0
   firstFrac = 1
   colored=""
   if es>0:
       exponent = 1
       e=0
   for c in orig:
       if m==0:
           colored+="\033[1;37;41m"+c+"\033[0m"
       elif regime==1:
           if m==1:
              regS = c
              colored+="\033[1;30;43m"+c
           else:
              if c==regS:
                 colored+=c
              else:
                 regime = 0
                 colored+=c+"\033[0m"           
       elif exponent==1:
           if e==0:
              colored+="\033[1;37;44m"+c
           else:
              colored+=c
           e+=1
           if e==es:
              colored+="\033[0m"
              exponent = 0
       else:
           if firstFrac==1:
              colored+="\033[1;37;40m"+c
              firstFrac=0
           else:
              colored+=c
       m+=1
       if (m!=ps and m%8==0):
           colored+=" "

   return colored+"\033[0m"
       
class posit8:
   def __init__(self, value=None, bits=None):
       try:
           if bits is not None:
               if isinstance(bits, (int)):
                   self.v = posit8_t()
                   self.v.v = bits & 0xFF
               else:
                   raise Exception("Bits can only be set with integer values")
           else:
               if value is None:
                   self.v = posit8_t()
                   self.v.v = 0
               elif isinstance(value, (int)):
                   self.v = _softposit.i64_to_p8(value)
               else:
                   self.v = _softposit.convertDoubleToP8(value)
       except Exception as error:
           print(repr(error))
   def type(self):
       return 'posit8'
   def __add__(self, other):
       try:
          a = posit8(0)
          if isinstance(other, (int)):
              a.v = _softposit.p8_add(self.v, _softposit.i64_to_p8(other))
          elif isinstance(other, (float)):
              a.v = _softposit.p8_add(self.v, _softposit.convertDoubleToP8(other))
          else:           
              a.v = _softposit.p8_add(self.v, other.v)
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for +: posit8 and ",other.type())          
   def __radd__(self, other):
       return self.__add__(other)  
   def __sub__(self, other):
       try:
          a = posit8(0)
          if isinstance(other, (int)):
              a.v = _softposit.p8_sub(self.v, _softposit.i64_to_p8(other))
          elif isinstance(other, (float)):
              a.v = _softposit.p8_sub(self.v, _softposit.convertDoubleToP8(other))
          else:
              a.v = _softposit.p8_sub(self.v, other.v)
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for -: posit8 and ",other.type())      
   def __rsub__(self, other):
       try:
          a = posit8(0)
          if isinstance(other, (int)):
              a.v = _softposit.p8_sub(_softposit.i64_to_p8(other), self.v)
          elif isinstance(other, (float)):
              a.v = _softposit.p8_sub(_softposit.convertDoubleToP8(other), self.v)
          else:
              a.v = _softposit.p8_sub(other.v, self.v)
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for -: posit8 and ",other.type())  
   def __mul__(self, other):
       try:
          a = posit8(0)
          if isinstance(other, (int)):
              a.v = _softposit.p8_mul(self.v, _softposit.i64_to_p8(other))
          elif isinstance(other, (float)):
              a.v = _softposit.p8_mul(self.v, _softposit.convertDoubleToP8(other))
          else:
              a.v = _softposit.p8_mul(self.v, other.v)
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for *: posit8 and ",other.type())   
   def __rmul__(self, other):
       return self.__mul__(other)  
   def __div__(self, other):
       try:
          a = posit8(0)
          if isinstance(other, (int)):
              a.v = _softposit.p8_div(self.v, _softposit.i64_to_p8(other))
          elif isinstance(other, (float)):
              a.v = _softposit.p8_div(self.v, _softposit.convertDoubleToP8(other))
          else:
              a.v = _softposit.p8_div(self.v, other.v)
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for /: posit8 and ",other.type())   
   def __truediv__(self, other):
       return self.__div__(other)      
   def __rdiv__(self, other):
       try:
          a = posit8(0)
          if isinstance(other, (int)):
              a.v = _softposit.p8_div(_softposit.i64_to_p8(other), self.v)
          elif isinstance(other, (float)):
              a.v = _softposit.p8_div(_softposit.convertDoubleToP8(other), self.v)
          else:
              a.v = _softposit.p8_div(other.v, self.v)
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for *: posit8 and ",other.type())  
   def __rtruediv__(self, other):
       return self.__rdiv__(other)
   def __eq__(self, other):
       try:
          if isinstance(other, (int)):
              return _softposit.p8_eq(self.v, _softposit.i64_to_p8(other))
          elif isinstance(other, (float)):
              return _softposit.p8_eq(self.v, _softposit.convertDoubleToP8(other))
          else:
              return _softposit.p8_eq(self.v, other.v)
       except TypeError:
          print("TypeError: Unsupported operand type(s) for ==: posit8 and ",other.type())   
   def __ne__(self, other):
       try:                                                                                     
          if isinstance(other, (int)):
              return not(_softposit.p8_eq(self.v, _softposit.i64_to_p8(other)))
          elif isinstance(other, (float)):
              return not(_softposit.p8_eq(self.v, _softposit.convertDoubleToP8(other)))
          else:
              return not(_softposit.p8_eq(self.v, other.v))
       except TypeError:
          print("TypeError: Unsupported operand type(s) for !=: posit8 and ",other.type())
   def __le__(self, other):
       try:        
          if isinstance(other, (int)):
              return _softposit.p8_le(self.v, _softposit.i64_to_p8(other))
          elif isinstance(other, (float)):
              return _softposit.p8_le(self.v, _softposit.convertDoubleToP8(other))
          else:
              return _softposit.p8_le(self.v, other.v)
       except TypeError:
          print("TypeError: Unsupported operand type(s) for <=: posit8 and ",other.type()) 
   def __lt__(self, other):
       try:   
          if isinstance(other, (int)):
              return _softposit.p8_lt(self.v, _softposit.i64_to_p8(other))
          elif isinstance(other, (float)):
              return _softposit.p8_lt(self.v, _softposit.convertDoubleToP8(other))
          else:
              return _softposit.p8_lt(self.v, other.v)
       except TypeError:
          print("TypeError: Unsupported operand type(s) for <: posit8 and ",other.type())
   def __ge__(self, other):
       try:   
          if isinstance(other, (int)):
              return _softposit.p8_le(_softposit.i64_to_p8(other), self.v)
          elif isinstance(other, (float)):
              return _softposit.p8_le(_softposit.convertDoubleToP8(other), self.v)
          else:
              return _softposit.p8_le(other.v, self.v)
       except TypeError:
          print("TypeError: Unsupported operand type(s) for >=: posit8 and ",other.type())
   def __gt__(self, other):
       try:   
          if isinstance(other, (int)):
              return _softposit.p8_lt(_softposit.i64_to_p8(other), self.v)
          elif isinstance(other, (float)):
              return _softposit.p8_lt(_softposit.convertDoubleToP8(other), self.v)
          else:
              return _softposit.p8_lt(other.v, self.v)
       except TypeError:
          print("TypeError: Unsupported operand type(s) for >: posit8 and ",other.type())
   def __rshift__(self, other):
       a = posit8(0)
       a.v = self.v.__rshift__(other)
       return a
   def __lshift__(self, other):
       a = posit8(0)
       a.v = self.v.__lshift__(other)
       return a
   def __pos__(self):
       return self
   def __neg__(self):
       a = posit8(0)
       a.v = self.v.__neg__()
       return a
   def __abs__(self):
       a = posit8(0)
       a.v = self.v.__abs__()
       return a
   def __invert__(self):
       self.v = self.v.__invert__()
       return self   
   def __and__(self, other):
       a = posit8(0)
       a.v = self.v.__and__(other.v)
       return a
   def __xor__(self, other):
       a = posit8(0)
       a.v = self.v.__xor__(other.v)
       return a
   def __or__(self, other):
       a = posit8(0)
       a.v = self.v.__or__(other.v)
       return a
   def fma(self, other1, other2):
       try:   
          a = posit8(0)
          if isinstance(other1, (int)):
              if isinstance(other2, (int)):
                  a.v = _softposit.p8_mulAdd(_softposit.i64_to_p8(other1), _softposit.i64_to_p8(other2), self.v)
              elif isinstance(other2, (float)):
                  a.v = _softposit.p8_mulAdd(_softposit.i64_to_p8(other1), _softposit.convertDoubleToP8(other2), self.v)
              else:
                  a.v = _softposit.p8_mulAdd(_softposit.i64_to_p8(other1), other2.v, self.v)
          elif isinstance(other1, (float)):
              if isinstance(other2, (int)):
                  a.v = _softposit.p8_mulAdd(_softposit.convertDoubleToP8(other1), _softposit.i64_to_p8(other2), self.v)
              elif isinstance(other2, (float)):
                  a.v = _softposit.p8_mulAdd(_softposit.convertDoubleToP8(other1), _softposit.convertDoubleToP8(other2), self.v)
              else:
                  a.v = _softposit.p8_mulAdd(_softposit.convertDoubleToP8(other1), other2.v, self.v)
          else:
              if isinstance(other2, (int)):
                  a.v = _softposit.p8_mulAdd(self.v, other1.v, _softposit.i64_to_p8(other2), self.v)
              elif isinstance(other2, (float)):
                  a.v = _softposit.p8_mulAdd(other1.v, _softposit.convertDoubleToP8(other2), self.v)
              else:
                  a.v = _softposit.p8_mulAdd(other1.v, other2.v, self.v)   
          return a
       except TypeError:
          print("TypeError: Unsupported fused operand (fma) among mixed precison posit types")
   def toPosit16(self):
       a = posit16(0)
       a.v = _softposit.p8_to_p16(self.v)
       return a
   def toPosit32(self):
       a = posit32(0)
       a.v = _softposit.p8_to_p32(self.v)
       return a
   def toPosit_2(self, x):
       a = posit_2(0, x)
       a.v = _softposit.p8_to_pX2(self.v, x)
       return a
   def toRInt(self):
       return _softposit.p8_to_i64(self.v)
   def toInt(self):
       return _softposit.p8_int(self.v)
   def rint(self):
       self.v = _softposit.p8_roundToInt(self.v)
       return self
   def sqrt(self):
       a = posit8(0)
       a.v = _softposit.p8_sqrt(self.v)
       return a
   def __repr__(self):
       a = float(_softposit.convertP8ToDouble(self.v))
       if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
           return "NaR"
       else:
           return str(a)
   def __str__(self):
       a = float(_softposit.convertP8ToDouble(self.v))
       if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
           return "NaR"
       else:
           return str(a)
   def __int__(self):
       return _softposit.p8_int(self.v)
   def __float__(self):
       return float(_softposit.convertP8ToDouble(self.v))
   def isNaR(self):
       return self.v.isNaR();
   def toNaR(self):
       a = posit8(0)
       a.v.toNaR();
       return a
   def fromBits(self, value):
       self.v.fromBits(value)
   def toBinary(self):
       self.v.toBits()
   def toBinaryFormatted(self):
       print(convertToColor(self.v.v, 8, 0))
   def toHex(self):
       self.v.toHex()

class quire8:
   def __init__(self):
       self.v = _softposit.q8Clr();
   def type(self):
       return 'quire8'
   def qma(self, other1, other2):
       try:
          a = posit8(0)
          if isinstance(other1, (int)):
              if isinstance(other2, (int)):
                  self.v = _softposit.q8_fdp_add(self.v, _softposit.i64_to_p8(other1), _softposit.i64_to_p8(other2))
              elif isinstance(other2, (float)):
                  self.v = _softposit.q8_fdp_add(self.v, _softposit.i64_to_p8(other1), _softposit.convertDoubleToP8(other2))
              else:
                  self.v = _softposit.q8_fdp_add(self.v, _softposit.i64_to_p8(other1), other2.v)
          elif isinstance(other1, (float)):
              if isinstance(other2, (int)):
                  self.v = _softposit.q8_fdp_add(self.v, _softposit.convertDoubleToP8(other1), _softposit.i64_to_p8(other2))
              elif isinstance(other2, (float)):
                  self.v = _softposit.q8_fdp_add(self.v, _softposit.convertDoubleToP8(other1), _softposit.convertDoubleToP8(other2))
              else:
                  self.v = _softposit.q8_fdp_add(self.v, _softposit.convertDoubleToP8(other1), other2.v)
          else:
              if isinstance(other2, (int)):
                  self.v = _softposit.q8_fdp_add(self.v, other1.v, _softposit.i64_to_p8(other2))
              elif isinstance(other2, (float)):
                  self.v = _softposit.q8_fdp_add(self.v, other1.v, _softposit.convertDoubleToP8(other2))
              else:
                  self.v = _softposit.q8_fdp_add(self.v, other1.v, other2.v)
          return self
       except TypeError:
          print("TypeError: Unsupported fused operand (qma) between quire8 and non-posit8 types")     
   def qms(self, other1, other2):
       try:
          a = posit8(0)
          if isinstance(other1, (int)):
              if isinstance(other2, (int)):
                  self.v = _softposit.q8_fdp_sub(self.v, _softposit.i64_to_p8(other1), _softposit.i64_to_p8(other2))
              elif isinstance(other2, (float)):
                  self.v = _softposit.q8_fdp_sub(self.v, _softposit.i64_to_p8(other1), _softposit.convertDoubleToP8(other2))
              else:
                  self.v = _softposit.q8_fdp_sub(self.v, _softposit.i64_to_p8(other1), other2.v)
          elif isinstance(other1, (float)):
              if isinstance(other2, (int)):
                  self.v = _softposit.q8_fdp_sub(self.v, _softposit.convertDoubleToP8(other1), _softposit.i64_to_p8(other2))
              elif isinstance(other2, (float)):
                  self.v = _softposit.q8_fdp_sub(self.v, _softposit.convertDoubleToP8(other1), _softposit.convertDoubleToP8(other2))
              else:
                  self.v = _softposit.q8_fdp_sub(self.v, _softposit.convertDoubleToP8(other1), other2.v)
          else:
              if isinstance(other2, (int)):
                  self.v = _softposit.q8_fdp_sub(self.v, other1.v, _softposit.i64_to_p8(other2))
              elif isinstance(other2, (float)):
                  self.v = _softposit.q8_fdp_sub(self.v, other1.v, _softposit.convertDoubleToP8(other2))
              else:
                  self.v = _softposit.q8_fdp_sub(self.v, other1.v, other2.v)
          return self
       except TypeError:
          print("TypeError: Unsupported fused operand (qms) between quire8 and non-posit8 types")    
   def toPosit(self): 
       a = posit8(0)      
       a.v = _softposit.q8_to_p8(self.v);
       return a
   def clr(self):       
       self.v = _softposit.q8Clr();
   def isNaR(self):       
       return self.v.isNaR();
   def __repr__(self):
       a = float(_softposit.convertP8ToDouble(_softposit.q8_to_p8(self.v)))
       if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
           return "NaR"
       else:
           return str(a)
   def __str__(self):
       a = float(_softposit.convertP8ToDouble(_softposit.q8_to_p8(self.v)))
       if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
           return "NaR"
       else:
           return str(a)
   def toBinary(self):
       self.v.toBits()
   def toHex(self):
       self.v.toHex()

class posit16:
   def __init__(self, value=None, bits=None):
       try:
           if bits is not None:
               if isinstance(bits, (int)):
                   self.v = posit16_t()
                   self.v.v = bits & 0xFFFF
               else:
                   raise Exception("Bits can only be set with integer values")
           else:
               if value is None:
                   self.v = posit16_t()
                   self.v.v = 0
               elif isinstance(value, (int)):
                   self.v = _softposit.i64_to_p16(value)
               else:
                   self.v = _softposit.convertDoubleToP16(value)
       except Exception as error:
           print(repr(error))
   def type(self):
       return 'posit16'
   def __add__(self, other):
       try:
          a = posit16(0)
          if isinstance(other, (int)):
              a.v = _softposit.p16_add(self.v, _softposit.i64_to_p16(other))
          elif isinstance(other, (float)):
              a.v = _softposit.p16_add(self.v, _softposit.convertDoubleToP16(other))
          else:
              a.v = _softposit.p16_add(self.v, other.v)
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for +: posit16 and ",other.type())     
   def __radd__(self, other):
       return self.__add__(other)  
   def __sub__(self, other):
       try:
          a = posit16(0)
          if isinstance(other, (int)):
              a.v = _softposit.p16_sub(self.v, _softposit.i64_to_p16(other))
          elif isinstance(other, (float)):
              a.v = _softposit.p16_sub(self.v, _softposit.convertDoubleToP16(other))
          else:
              a.v = _softposit.p16_sub(self.v, other.v)
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for -: posit16 and ",other.type()) 
   def __rsub__(self, other):
       try:
          a = posit16(0)
          if isinstance(other, (int)):
              a.v = _softposit.p16_sub(_softposit.i64_to_p16(other))
          elif isinstance(other, (float)):
              a.v = _softposit.p16_sub(_softposit.convertDoubleToP16(other), self.v)
          else:
              a.v = _softposit.p16_sub(other.v, self.v)
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for -: posit16 and ",other.type())  
   def __mul__(self, other):
       try:
          a = posit16(0)
          if isinstance(other, (int)):
              a.v = _softposit.p16_mul(self.v, _softposit.i64_to_p16(other))
          elif isinstance(other, (float)):
              a.v = _softposit.p16_mul(self.v, _softposit.convertDoubleToP16(other))
          else:
              a.v = _softposit.p16_mul(self.v, other.v)
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for *: posit16 and ",other.type()) 
   def __rmul__(self, other):
       return self.__mul__(other)  
   def __div__(self, other):
       try:
          a = posit16(0)
          if isinstance(other, (int)):
              a.v = _softposit.p16_div(self.v, _softposit.i64_to_p16(other))
          elif isinstance(other, (float)):
              a.v = _softposit.p16_div(self.v, _softposit.convertDoubleToP16(other))
          else:
              a.v = _softposit.p16_div(self.v, other.v)
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for /: posit16 and ",other.type()) 
   def __truediv__(self, other):
       return self.__div__(other)
   def __rdiv__(self, other):
       try:
          a = posit16(0)
          if isinstance(other, (int)):
              a.v = _softposit.p16_div(_softposit.i64_to_p16(other), self.v)
          elif isinstance(other, (float)):
              a.v = _softposit.p16_div(_softposit.convertDoubleToP16(other), self.v)
          else:
              a.v = _softposit.p16_div(other.v, self.v)
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for /: posit16 and ",other.type()) 
   def __rtruediv__(self, other):
       return self.__rdiv__(other)
   def __eq__(self, other):
       try:
          if isinstance(other, (int)):
              return _softposit.p16_eq(self.v, _softposit.i64_to_p16(other))
          elif isinstance(other, (float)):
              return _softposit.p16_eq(self.v, _softposit.convertDoubleToP16(other))
          else:
              return _softposit.p16_eq(self.v, other.v)
       except TypeError:
          print("TypeError: Unsupported operand type(s) for ==: posit16 and ",other.type()) 
   def __ne__(self, other):  
       try:                                                                                          
          if isinstance(other, (int)):
              return not(_softposit.p16_eq(self.v, _softposit.i64_to_p16(other)))
          elif isinstance(other, (float)):
              return not(_softposit.p16_eq(self.v, _softposit.convertDoubleToP16(other)))
          else:
              return not(_softposit.p16_eq(self.v, other.v))
       except TypeError:
          print("TypeError: Unsupported operand type(s) for !=: posit16 and ",other.type())
   def __le__(self, other):
       try:             
          if isinstance(other, (int)):
              return _softposit.p16_le(self.v, _softposit.i64_to_p16(other))
          elif isinstance(other, (float)):
              return _softposit.p16_le(self.v, _softposit.convertDoubleToP16(other))
          else:
              return _softposit.p16_le(self.v, other.v)
       except TypeError:
          print("TypeError: Unsupported operand type(s) for <=: posit16 and ",other.type()) 
   def __lt__(self, other):
       try:             
          if isinstance(other, (int)):
              return _softposit.p16_lt(self.v, _softposit.i64_to_p16(other))
          elif isinstance(other, (float)):
              return _softposit.p16_lt(self.v, _softposit.convertDoubleToP16(other))
          else:
              return _softposit.p16_lt(self.v, other.v)
       except TypeError:
          print("TypeError: Unsupported operand type(s) for <: posit16 and ",other.type()) 
   def __ge__(self, other):
       try:    
          if isinstance(other, (int)):
              return _softposit.p16_le(_softposit.i64_to_p16(other), self.v)
          elif isinstance(other, (float)):
              return _softposit.p16_le(_softposit.convertDoubleToP16(other), self.v)
          else:
              return _softposit.p16_le(other.v, self.v)
       except TypeError:
          print("TypeError: Unsupported operand type(s) for >=: posit16 and ",other.type())  
   def __gt__(self, other):
       if isinstance(other, (int)):
           return _softposit.p16_lt(_softposit.i64_to_p16(other), self.v)
       elif isinstance(other, (float)):
           return _softposit.p16_lt(_softposit.convertDoubleToP16(other), self.v)
       else:
           return _softposit.p16_lt(other.v, self.v)
   def __rshift__(self, other):
       a = posit16(0)
       a.v = self.v.__rshift__(other)
       return a
   def __lshift__(self, other):
       a = posit16(0)
       a.v = self.v.__lshift__(other)
       return a
   def __pos__(self):
       return self
   def __neg__(self):
       a = posit16(0)
       a.v = self.v.__neg__()
       return a
   def __abs__(self):
       a = posit16(0)
       a.v = self.v.__abs__()
       return a
   def __invert__(self):
       self.v = self.v.__invert__()
       return self   
   def __and__(self, other):
       a = posit16(0)
       a.v = self.v.__and__(other.v)
       return a
   def __xor__(self, other):
       a = posit16(0)
       a.v = self.v.__xor__(other.v)
       return a
   def __or__(self, other):
       a = posit16(0)
       a.v = self.v.__or__(other.v)
       return a
   def fma(self, other1, other2):
       try:
          a = posit16(0)
          if isinstance(other1, (int)):
              if isinstance(other2, (int)):
                  a.v = _softposit.p16_mulAdd(_softposit.i64_to_p16(other1), _softposit.i64_to_p16(other2), self.v)
              elif isinstance(other2, (float)):
                  a.v = _softposit.p16_mulAdd(_softposit.i64_to_p16(other1), _softposit.convertDoubleToP16(other2), self.v)
              else:
                  a.v = _softposit.p16_mulAdd(_softposit.i64_to_p16(other1), other2.v, self.v)
          elif isinstance(other1, (float)):
              if isinstance(other2, (int)):
                  a.v = _softposit.p16_mulAdd(_softposit.convertDoubleToP16(other1), _softposit.i64_to_p16(other2), self.v)
              elif isinstance(other2, (float)):
                  a.v = _softposit.p16_mulAdd(_softposit.convertDoubleToP16(other1), _softposit.convertDoubleToP16(other2), self.v)
              else:
                  a.v = _softposit.p16_mulAdd(_softposit.convertDoubleToP16(other1), other2.v, self.v)
          else:
              if isinstance(other2, (int)):
                  a.v = _softposit.p16_mulAdd(other1.v, _softposit.i64_to_p16(other2), self.v)
              elif isinstance(other2, (float)):
                  a.v = _softposit.p16_mulAdd(other1.v, _softposit.convertDoubleToP16(other2), self.v)
              else:
                  a.v = _softposit.p16_mulAdd(other1.v, other2.v, self.v)
          return a
       except TypeError:
          print("TypeError: Unsupported fused operand (fma) among mixed precison posit types")
   def toPosit8(self):
       a = posit8(0)
       a.v = _softposit.p16_to_p8(self.v)
       return a
   def toPosit32(self):
       a = posit32(0)
       a.v = _softposit.p16_to_p32(self.v)
       return a
   def toPosit_2(self, x):
       a = posit_2(0, x)
       a.v = _softposit.p16_to_pX2(self.v, x)
       return a
   def toRInt(self):
       return _softposit.p16_to_i64(self.v)
   def toInt(self):
       return _softposit.p16_int(self.v)
   def rint(self):
       self.v = _softposit.p16_roundToInt(self.v)
       return self
   def sqrt(self):
       a = posit16(0)
       a.v = _softposit.p16_sqrt(self.v)
       return a
   def __repr__(self):
       a = float(_softposit.convertP16ToDouble(self.v))
       if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
           return "NaR"
       else:
           return str(a)
   def __str__(self):
       a = float(_softposit.convertP16ToDouble(self.v))
       if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
           return "NaR"
       else:
           return str(a)
   def __int__(self):
       return _softposit.p16_int(self.v) 
   def __float__(self):
       return float(_softposit.convertP16ToDouble(self.v))
   def isNaR(self):
       return self.v.isNaR()
   def toNaR(self):
       a = posit16(0)
       a.v.toNaR()
       return a
   def fromBits(self, value):
       self.v.fromBits(value)
   def toBinary(self):
       self.v.toBits()
   def toBinaryFormatted(self):
       print(convertToColor(self.v.v, 16, 1))
   def toHex(self):
       self.v.toHex()


class quire16:
   def __init__(self):
       self.v = _softposit.q16Clr();
   def type(self):
       return 'quire16'
   def qma(self, other1, other2):
       try:
          a = posit16(0)
          if isinstance(other1, (int)):
              if isinstance(other2, (int)):
                  self.v = _softposit.q16_fdp_add(self.v, _softposit.i64_to_p16(other1), _softposit.i64_to_p16(other2))
              elif isinstance(other2, (float)):
                  self.v = _softposit.q16_fdp_add(self.v, _softposit.i64_to_p16(other1), _softposit.convertDoubleToP16(other2))
              else:
                  self.v = _softposit.q16_fdp_add(self.v, _softposit.i64_to_p16(other1), other2.v)
          elif isinstance(other1, (float)):
              if isinstance(other2, (int)):
                  self.v = _softposit.q16_fdp_add(self.v, _softposit.convertDoubleToP16(other1), _softposit.i64_to_p16(other2))
              elif isinstance(other2, (float)):
                  self.v = _softposit.q16_fdp_add(self.v, _softposit.convertDoubleToP16(other1), _softposit.convertDoubleToP16(other2))
              else:
                  self.v = _softposit.q16_fdp_add(self.v, _softposit.convertDoubleToP16(other1), other2.v)
          else:
              if isinstance(other2, (int)):
                  self.v = _softposit.q16_fdp_add(self.v, other1.v, _softposit.i64_to_p16(other2))
              elif isinstance(other2, (float)):
                  self.v = _softposit.q16_fdp_add(self.v, other1.v, _softposit.convertDoubleToP16(other2))
              else:
                  self.v = _softposit.q16_fdp_add(self.v, other1.v, other2.v)
          return self
       except TypeError:
          print("TypeError: Unsupported fused operand (qms) between quire16 and non-posit16 types")  
   def qms(self, other1, other2):
       try:
          a = posit16(0)
          if isinstance(other1, (int)):
              if isinstance(other2, (int)):
                  self.v = _softposit.q16_fdp_sub(self.v, _softposit.i64_to_p16(other1), _softposit.i64_to_p16(other2))
              elif isinstance(other2, (float)):
                  self.v = _softposit.q16_fdp_sub(self.v, _softposit.i64_to_p16(other1), _softposit.convertDoubleToP16(other2))
              else:
                  self.v = _softposit.q16_fdp_sub(self.v, _softposit.i64_to_p16(other1), other2.v)
          elif isinstance(other1, (float)):
              if isinstance(other2, (int)):
                  self.v = _softposit.q16_fdp_sub(self.v, _softposit.convertDoubleToP16(other1), _softposit.i64_to_p16(other2))
              elif isinstance(other2, (float)):
                  self.v = _softposit.q16_fdp_sub(self.v, _softposit.convertDoubleToP16(other1), _softposit.convertDoubleToP16(other2))
              else:
                  self.v = _softposit.q16_fdp_sub(self.v, _softposit.convertDoubleToP16(other1), other2.v)
          else:
              if isinstance(other2, (int)):
                  self.v = _softposit.q16_fdp_sub(self.v, other1.v, _softposit.i64_to_p16(other2))
              elif isinstance(other2, (float)):
                  self.v = _softposit.q16_fdp_sub(self.v, other1.v, _softposit.convertDoubleToP16(other2))
              else:
                  self.v = _softposit.q16_fdp_sub(self.v, other1.v, other2.v)
          return self
       except TypeError:
          print("TypeError: Unsupported fused operand (qms) between quire16 and non-posit16 types")   
   def toPosit(self): 
       a = posit16(0)      
       a.v = _softposit.q16_to_p16(self.v);
       return a
   def clr(self):       
       self.v = _softposit.q16Clr();
   def isNaR(self):       
       return self.v.isNaR();
   def __repr__(self):
       a = float(_softposit.convertP16ToDouble(_softposit.q16_to_p16(self.v)))
       if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
           return "NaR"
       else:
           return str(a)
   def __str__(self):
       a = float(_softposit.convertP16ToDouble(_softposit.q16_to_p16(self.v)))
       if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
           return "NaR"
       else:
           return str(a)
   def toBinary(self):
       self.v.toBits()
   def toHex(self):
       self.v.toHex()

class posit32:
   def __init__(self, value=None, bits=None):
       try:
           if bits is not None:
               if isinstance(bits, (int)):
                   self.v = posit32_t()
                   self.v.v = bits&0xFFFFFFFF
               else:
                   raise Exception("Bits can only be set with integer values")
           else:
               if value is None:
                   self.v = posit32_t()
                   self.v.v = 0
               elif isinstance(value, (int)):
                   self.v = _softposit.i64_to_p32(value)
               else:
                   self.v = _softposit.convertDoubleToP32(value)
       except Exception as error:
           print(repr(error))
   def type(self):
       return 'posit32'
   def __add__(self, other):
       try:
          a = posit32(0)
          if isinstance(other, (int)):
              a.v = _softposit.p32_add(self.v, _softposit.i64_to_p32(other))
          elif isinstance(other, (float)):
              a.v = _softposit.p32_add(self.v, _softposit.convertDoubleToP32(other))
          else:
              a.v = _softposit.p32_add(self.v, other.v)
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for +: posit32 and ",other.type())     
   def __radd__(self, other):
       return self.__add__(other)  
   def __sub__(self, other):
       try:
          a = posit32(0)
          if isinstance(other, (int)):
              a.v = _softposit.p32_sub(self.v, _softposit.i64_to_p32(other))
          elif isinstance(other, (float)):
              a.v = _softposit.p32_sub(self.v, _softposit.convertDoubleToP32(other))
          else:
              a.v = _softposit.p32_sub(self.v, other.v)
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for -: posit32 and ",other.type())  
   def __rsub__(self, other):
       try:
          a = posit32(0)
          if isinstance(other, (int)):
              a.v = _softposit.p32_sub(_softposit.i64_to_p32(other), self.v)
          elif isinstance(other, (float)):
              a.v = _softposit.p32_sub(_softposit.convertDoubleToP32(other), self.v)
          else:
              a.v = _softposit.p32_sub(other.v, self.v)
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for -: posit32 and ",other.type())    
   def __mul__(self, other):
       try:
          a = posit32(0)
          if isinstance(other, (int)):
              a.v = _softposit.p32_mul(self.v, _softposit.i64_to_p32(other))
          elif isinstance(other, (float)):
              a.v = _softposit.p32_mul(self.v, _softposit.convertDoubleToP32(other))
          else:
              a.v = _softposit.p32_mul(self.v, other.v)
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for *: posit32 and ",other.type())     
   def __rmul__(self, other):
       return self.__mul__(other)  
   def __div__(self, other):
       try:
          a = posit32(0)
          if isinstance(other, (int)):
              a.v = _softposit.p32_div(self.v, _softposit.i64_to_p32(other))
          elif isinstance(other, (float)):
              a.v = _softposit.p32_div(self.v, _softposit.convertDoubleToP32(other))
          else:
              a.v = _softposit.p32_div(self.v, other.v)
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for /: posit32 and ",other.type())   
   def __truediv__(self, other):
       return self.__div__(other)
   def __rdiv__(self, other):
       try:
          a = posit32(0)
          if isinstance(other, (int)):
              a.v = _softposit.p32_div(_softposit.i64_to_p32(other), self.v)
          elif isinstance(other, (float)):
              a.v = _softposit.p32_div(_softposit.convertDoubleToP32(other), self.v)
          else:
              a.v = _softposit.p32_div(other.v, self.v)
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for /: posit32 and ",other.type())     
   def __rtruediv__(self, other):
       return self.__rdiv__(other)
   def __eq__(self, other):
       try:
          if isinstance(other, (int)):
              return _softposit.p32_eq(self.v, _softposit.i64_to_p32(other))
          elif isinstance(other, (float)):
              return _softposit.p32_eq(self.v, _softposit.convertDoubleToP32(other))
          else:
              return _softposit.p32_eq(self.v, other.v)
       except TypeError:
          print("TypeError: Unsupported operand type(s) for ==: posit32 and ",other.type())     
   def __ne__(self, other):  
       try:                                                                                
          if isinstance(other, (int)):
              return not(_softposit.p32_eq(self.v, _softposit.i64_to_p32(other)))
          elif isinstance(other, (float)):
              return not(_softposit.p32_eq(self.v, _softposit.convertDoubleToP32(other)))
          else:
              return not(_softposit.p32_eq(self.v, other.v))
       except TypeError:
          print("TypeError: Unsupported operand type(s) for !=: posit32 and ",other.type())    
   def __le__(self, other):
       try:
          if isinstance(other, (int)):
              return _softposit.p32_le(self.v, _softposit.i64_to_p32(other))
          elif isinstance(other, (float)):
              return _softposit.p32_le(self.v, _softposit.convertDoubleToP32(other))
          else:
              return _softposit.p32_le(self.v, other.v)
       except TypeError:
          print("TypeError: Unsupported operand type(s) for <=: posit32 and ",other.type())  
   def __lt__(self, other):
       try:
          if isinstance(other, (int)):
              return _softposit.p32_lt(self.v, _softposit.i64_to_p32(other))
          elif isinstance(other, (float)):
              return _softposit.p32_lt(self.v, _softposit.convertDoubleToP32(other))
          else:
              return _softposit.p32_lt(self.v, other.v)
       except TypeError:
          print("TypeError: Unsupported operand type(s) for <: posit32 and ",other.type())  
   def __ge__(self, other):
       try:
          if isinstance(other, (int)):
              return _softposit.p32_le(_softposit.i64_to_p32(other), self.v)
          elif isinstance(other, (float)):
              return _softposit.p32_le(_softposit.convertDoubleToP32(other), self.v)
          else:
              return _softposit.p32_le(other.v, self.v)
       except TypeError:
          print("TypeError: Unsupported operand type(s) for >=: posit32 and ",other.type())  
   def __gt__(self, other):
       try:
          if isinstance(other, (int)):
              return _softposit.p32_lt(_softposit.i64_to_p32(other), self.v)
          elif isinstance(other, (float)):
              return _softposit.p32_lt(_softposit.convertDoubleToP32(other), self.v)
          else:
              return _softposit.p32_lt(other.v, self.v)
       except TypeError:
          print("TypeError: Unsupported operand type(s) for >: posit32 and ",other.type())  
   def __rshift__(self, other):
       a = posit32(0)
       a.v = self.v.__rshift__(other)
       return a
   def __lshift__(self, other):
       a = posit32(0)
       a.v = self.v.__lshift__(other)
       return a
   def __pos__(self):
       return self
   def __neg__(self):
       a = posit32(0)
       a.v = self.v.__neg__()
       return a
   def __abs__(self):
       a = posit32(0)
       a.v = self.v.__abs__()
       return a
   def __invert__(self):
       self.v = self.v.__invert__()
       return self   
   def __and__(self, other):
       a = posit32(0)
       a.v = self.v.__and__(other.v)
       return a
   def __xor__(self, other):
       a = posit32(0)
       a.v = self.v.__xor__(other.v)
       return a
   def __or__(self, other):
       a = posit32(0)
       a.v = self.v.__or__(other.v)
       return a
   def fma(self, other1, other2):
       try:
          a = posit32(0)
          if isinstance(other1, (int)):
              if isinstance(other2, (int)):
                  a.v = _softposit.p32_mulAdd(_softposit.i64_to_p32(other1), _softposit.i64_to_p32(other2), self.v)
              elif isinstance(other2, (float)):
                  a.v = _softposit.p32_mulAdd(_softposit.i64_to_p32(other1), _softposit.convertDoubleToP32(other2), self.v)
              else:
                  a.v = _softposit.p32_mulAdd(_softposit.i64_to_p32(other1), other2.v, self.v)
          elif isinstance(other1, (float)):
              if isinstance(other2, (int)):
                  a.v = _softposit.p32_mulAdd(_softposit.convertDoubleToP32(other1), _softposit.i64_to_p32(other2), self.v)
              elif isinstance(other2, (float)):
                  a.v = _softposit.p32_mulAdd(_softposit.convertDoubleToP32(other1), _softposit.convertDoubleToP32(other2), self.v)
              else:
                  a.v = _softposit.p32_mulAdd(_softposit.convertDoubleToP32(other1), other2.v, self.v)
          else:
              if isinstance(other2, (int)):
                  a.v = _softposit.p32_mulAdd(other1.v, _softposit.i64_to_p32(other2), self.v)
              elif isinstance(other2, (float)):
                  a.v = _softposit.p32_mulAdd(other1.v, _softposit.convertDoubleToP32(other2), self.v)
              else:
                  a.v = _softposit.p32_mulAdd(other1.v, other2.v, self.v)   
          return a
       except TypeError:
          print("TypeError: Unsupported fused operand (fma) among mixed precison posit types")
   def toPosit8(self):
       a = posit8(0)
       a.v = _softposit.p32_to_p8(self.v)
       return a
   def toPosit16(self):
       a = posit16(0)
       a.v = _softposit.p32_to_p16(self.v)
       return a
   def toPosit_2(self, x):
       a = posit_2(0, x)
       a.v = _softposit.p32_to_pX2(self.v, x)
       a.x = x
       return a
   def toRInt(self):
       return _softposit.p32_to_i64(self.v)
   def toInt(self):
       return _softposit.p32_int(self.v)
   def rint(self):
       self.v = _softposit.p32_roundToInt(self.v)
       return self
   def sqrt(self):
       a = posit32(0)
       a.v = _softposit.p32_sqrt(self.v)
       return a
   def __repr__(self):
       a = float(_softposit.convertP32ToDouble(self.v))
       if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
           return "NaR"
       else:
           return str(a)
   def __str__(self):
       a = float(_softposit.convertP32ToDouble(self.v))
       if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
           return "NaR"
       else:
           return str(a)
   def __int__(self):
       return _softposit.p32_int(self.v) 
   def __float__(self):
       return float(_softposit.convertP32ToDouble(self.v))
   def isNaR(self):
       return self.v.isNaR();
   def toNaR(self):
       a = posit32(0)
       a.v.toNaR();
       return a
   def toBinary(self):
       self.v.toBits()
   def fromBits(self, value):
       self.v.fromBits(value)
   def toBinaryFormatted(self):
       print(convertToColor(self.v.v, 32, 2))
   def toHex(self):
       self.v.toHex()

class quire32:
   def __init__(self):
       self.v = _softposit.q32Clr();
   def type(self):
       return 'quire32'
   def qma(self, other1, other2):
       try:
          a = posit32(0)
          if isinstance(other1, (int)):
              if isinstance(other2, (int)):
                  self.v = _softposit.q32_fdp_add(self.v, _softposit.i64_to_p32(other1), _softposit.i64_to_p32(other2))
              elif isinstance(other2, (float)):
                  self.v = _softposit.q32_fdp_add(self.v, _softposit.i64_to_p32(other1), _softposit.convertDoubleToP32(other2))
              else:
                  self.v = _softposit.q32_fdp_add(self.v, _softposit.i64_to_p32(other1), other2.v)
          elif isinstance(other1, (float)):
              if isinstance(other2, (int)):
                  self.v = _softposit.q32_fdp_add(self.v, _softposit.convertDoubleToP32(other1), _softposit.i64_to_p32(other2))
              elif isinstance(other2, (float)):
                  self.v = _softposit.q32_fdp_add(self.v, _softposit.convertDoubleToP32(other1), _softposit.convertDoubleToP32(other2))
              else:
                  self.v = _softposit.q32_fdp_add(self.v, _softposit.convertDoubleToP32(other1), other2.v)
          else:
              if isinstance(other2, (int)):
                  self.v = _softposit.q32_fdp_add(self.v, other1.v, _softposit.i64_to_p32(other2))
              elif isinstance(other2, (float)):
                  self.v = _softposit.q32_fdp_add(self.v, other1.v, _softposit.convertDoubleToP32(other2))
              else:
                  self.v = _softposit.q32_fdp_add(self.v, other1.v, other2.v)
          return self
       except TypeError:
          print("TypeError: Unsupported fused operand (qma) between quire32 and non-posit32 types")   
   def qms(self, other1, other2):
       try:
          a = posit32(0)
          if isinstance(other1, (int)):
              if isinstance(other2, (int)):
                  self.v = _softposit.q32_fdp_sub(self.v, _softposit.i64_to_p32(other1), _softposit.i64_to_p32(other2))
              elif isinstance(other2, (float)):
                  self.v = _softposit.q32_fdp_sub(self.v, _softposit.i64_to_p32(other1), _softposit.convertDoubleToP32(other2))
              else:
                  self.v = _softposit.q32_fdp_sub(self.v, _softposit.i64_to_p32(other1), other2.v)
          elif isinstance(other1, (float)):
              if isinstance(other2, (int)):
                  self.v = _softposit.q32_fdp_sub(self.v, _softposit.convertDoubleToP32(other1), _softposit.i64_to_p32(other2))
              elif isinstance(other2, (float)):
                  self.v = _softposit.q32_fdp_sub(self.v, _softposit.convertDoubleToP32(other1), _softposit.convertDoubleToP32(other2))
              else:
                  self.v = _softposit.q32_fdp_sub(self.v, _softposit.convertDoubleToP32(other1), other2.v)
          else:
              if isinstance(other2, (int)):
                  self.v = _softposit.q32_fdp_sub(self.v, other1.v, _softposit.i64_to_p32(other2))
              elif isinstance(other2, (float)):
                  self.v = _softposit.q32_fdp_sub(self.v, other1.v, _softposit.convertDoubleToP32(other2))
              else:
                  self.v = _softposit.q32_fdp_sub(self.v, other1.v, other2.v)
          return self
       except TypeError:
           print("TypeError: Unsupported fused operand (qmd) between quire32 and non-posit32 types")   
   def toPosit(self):   
       a = posit32(0)
       a.v = _softposit.q32_to_p32(self.v);
       return a
   def clr(self):       
       self.v = _softposit.q32Clr();
   def isNaR(self):       
       return self.v.isNaR();
   def __repr__(self):
       a = float(_softposit.convertP32ToDouble(_softposit.q32_to_p32(self.v)))
       if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
           return "NaR"
       else:
           return str(a)
   def __str__(self):
       a = float(_softposit.convertP32ToDouble(_softposit.q32_to_p32(self.v)))
       if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
           return "NaR"
       else:
           return str(a)
   def toBinary(self):
       self.v.toBits()
   def toHex(self):
       self.v.toHex()


class posit_2:
   def __init__(self, value=None, x=None, bits=None):
       self.x = x  
       try:
           if x is None:
               raise Exception("Needs to specific posit size, x")
           if bits is not None:
               if isinstance(bits, (int)):
                   self.v = posit_2_t()
                   self.v.v = bits<<(32-x)
               else:
                   raise Exception("Bits can only be set with integer values")
           else:
               if value is None:
                   self.v = posit_2_t()
                   self.v.v = 0
               elif isinstance(value, (int)):
                   self.v = _softposit.i64_to_pX2(value, x)
               else:
                   self.v = _softposit.convertDoubleToPX2(value, x)
       except Exception as error:
           print(repr(error))
   def type(self):
       return 'posit' + str(self.x) +'_2'
   def __add__(self, other):
       try:
          a = posit_2(0, self.x)
          if isinstance(other, (int)):
              a.v = _softposit.pX2_add(self.v, _softposit.i64_to_pX2(other, self.x), self.x)
          elif isinstance(other, (float)):
              a.v = _softposit.pX2_add(self.v, _softposit.convertDoubleToPX2(other, self.x), self.x)
          else:
              if(self.x==other.x):
                  a.v = _softposit.pX2_add(self.v, other.v, self.x)
              else:	             
                  print("TypeError:  Unsupported operand type(s) for +: posit"+str(self.x)+"_2 and ",other.type()) 
                  a.v.isNaR();
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for +: posit"+str(self.x)+"_2 and ",other.type())          
   def __radd__(self, other):
       return self.__add__(other)  
   def __sub__(self, other):
       try:
          a = posit_2(0, self.x)
          if isinstance(other, (int)):
              a.v = _softposit.pX2_sub(self.v, _softposit.i64_to_pX2(other, self.x), self.x)
          elif isinstance(other, (float)):
              a.v = _softposit.pX2_sub(self.v, _softposit.convertDoubleToPX2(other, self.x), self.x)
          else:
              if(self.x==other.x):
                  a.v = _softposit.pX2_sub(self.v, other.v, self.x)
              else:	             
                  print("TypeError:  Unsupported operand type(s) for +: posit"+str(self.x)+"_2 and ",other.type()) 
                  a.v.isNaR();              
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for -: posit_2 and ",other.type())      
   def __rsub__(self, other):
       try:
          a = posit_2(0, self.x)
          if isinstance(other, (int)):
              a.v = _softposit.pX2_sub(_softposit.i64_to_pX2(other, self.x), self.v, self.x)
          elif isinstance(other, (float)):
              a.v = _softposit.pX2_sub(_softposit.convertDoubleToPX2(other, self.x), self.v, self.x)
          else:
              if(self.x==other.x):
                  a.v = _softposit.pX2_sub(other.v, self.v, self.x)
              else:	             
                  print("TypeError:  Unsupported operand type(s) for +: posit"+str(self.x)+"_2 and ",other.type()) 
                  a.v.isNaR();              
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for -: posit_2 and ",other.type())  
   def __mul__(self, other):
       try:
          a = posit_2(0, self.x)
          if isinstance(other, (int)):
              a.v = _softposit.pX2_mul(self.v, _softposit.i64_to_pX2(other, self.x), self.x)
          elif isinstance(other, (float)):
              a.v = _softposit.pX2_mul(self.v, _softposit.convertDoubleToPX2(other, self.x), self.x)
          else:              
              if(self.x==other.x):
                  a.v = _softposit.pX2_mul(self.v, other.v, self.x)
              else:	             
                  print("TypeError:  Unsupported operand type(s) for +: posit"+str(self.x)+"_2 and ",other.type()) 
                  a.v.isNaR();       
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for *: posit_2 and ",other.type())   
   def __rmul__(self, other):
       return self.__mul__(other)  
   def __div__(self, other):
       try:
          a = posit_2(0, self.x)
          if isinstance(other, (int)):
              a.v = _softposit.pX2_div(self.v, _softposit.i64_to_pX2(other, self.x), self.x)
          elif isinstance(other, (float)):
              a.v = _softposit.pX2_div(self.v, _softposit.convertDoubleToPX2(other, self.x), self.x)
          else:
              if(self.x==other.x):
                  a.v = _softposit.pX2_div(self.v, other.v, self.x)
              else:	             
                  print("TypeError:  Unsupported operand type(s) for +: posit"+str(self.x)+"_2 and ",other.type()) 
                  a.v.isNaR();       
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for /: posit_2 and ",other.type())   
   def __truediv__(self, other):
       return self.__div__(other)
   def __rdiv__(self, other):
       try:
          a = posit_2(0, self.x)
          if isinstance(other, (int)):
              a.v = _softposit.pX2_div(_softposit.i64_to_pX2(other, self.x), self.v, self.x)
          elif isinstance(other, (float)):
              a.v = _softposit.pX2_div(_softposit.convertDoubleToPX2(other, self.x), self.v, self.x)
          else:              
              if(self.x==other.x):
                  a.v = _softposit.pX2_div(other.v, self.v, self.x)
              else:	             
                  print("TypeError:  Unsupported operand type(s) for +: posit"+str(self.x)+"_2 and ",other.type()) 
                  a.v.isNaR();       
          return a
       except TypeError:
          print("TypeError: Unsupported operand type(s) for *: posit"+str(self.x)+"_2 and ",other.type())  
   def __rtruediv__(self, other):
       return self.__rdiv__(other)
   def __eq__(self, other):
       try:
          if isinstance(other, (int)):
              return _softposit.pX2_eq(self.v, _softposit.i64_to_pX2(other, self.x))
          elif isinstance(other, (float)):
              return _softposit.pX2_eq(self.v, _softposit.convertDoubleToPX2(other, self.x))
          else:
              return _softposit.pX2_eq(self.v, other.v)
       except TypeError:
          print("TypeError: Unsupported operand type(s) for ==: posit"+str(self.x)+"_2 and ",other.type())   
   def __ne__(self, other):
       try:                                                                                     
          if isinstance(other, (int)):
              return not(_softposit.pX2_eq(self.v, _softposit.i64_to_pX2(other, self.x)))
          elif isinstance(other, (float)):
              return not(_softposit.pX2_eq(self.v, _softposit.convertDoubleToPX2(other, self.x)))
          else:
              return not(_softposit.pX2_eq(self.v, other.v))
       except TypeError:
          print("TypeError: Unsupported operand type(s) for !=: posit"+str(self.x)+"_2 and ",other.type())
   def __le__(self, other):
       try:        
          if isinstance(other, (int)):
              return _softposit.pX2_le(self.v, _softposit.i64_to_pX2(other, self.x))
          elif isinstance(other, (float)):
              return _softposit.pX2_le(self.v, _softposit.convertDoubleToPX2(other, self.x))
          else:
              return _softposit.pX2_le(self.v, other.v)
       except TypeError:
          print("TypeError: Unsupported operand type(s) for <=: posit"+str(self.x)+"_2 and ",other.type()) 
   def __lt__(self, other):
       try:   
          if isinstance(other, (int)):
              return _softposit.pX2_lt(self.v, _softposit.i64_to_pX2(other, self.x))
          elif isinstance(other, (float)):
              return _softposit.pX2_lt(self.v, _softposit.convertDoubleToPX2(other, self.x))
          else:
              return _softposit.pX2_lt(self.v, other.v)
       except TypeError:
          print("TypeError: Unsupported operand type(s) for <: posit"+str(self.x)+"_2 and ",other.type())
   def __ge__(self, other):
       try:   
          if isinstance(other, (int)):
              return _softposit.pX2_le(_softposit.i64_to_pX2(other, self.x), self.v)
          elif isinstance(other, (float)):
              return _softposit.pX2_le(_softposit.convertDoubleToPX2(other, self.x), self.v)
          else:
              return _softposit.pX2_le(other.v, self.v)
       except TypeError:
          print("TypeError: Unsupported operand type(s) for >=: posit"+str(self.x)+"_2 and ",other.type())
   def __gt__(self, other):
       try:   
          if isinstance(other, (int)):
              return _softposit.pX2_lt(_softposit.i64_to_pX2(other, self.x), self.v)
          elif isinstance(other, (float)):
              return _softposit.pX2_lt(_softposit.convertDoubleToPX2(other, self.x), self.v)
          else:
              return _softposit.pX2_lt(other.v, self.v)
       except TypeError:
          print("TypeError: Unsupported operand type(s) for >: posit"+str(self.x)+"_2 and ",other.type())
   def __rshift__(self, other):
       a = posit_2(0, self.x)
       a.v = self.v.__rshift__(other, self.x)
       return a
   def __lshift__(self, other):
       a = posit_2(0, self.x)
       a.v = self.v.__lshift__(other, self.x)
       return a
   def __pos__(self):
       return self
   def __neg__(self):
       a = posit_2(0, self.x)
       a.v = self.v.__neg__(self.x)
       return a
   def __abs__(self):
       a = posit_2(0, self.x)
       a.v = self.v.__abs__(self.x)
       return a
   def __invert__(self):
       self.v = self.v.__invert__(self.x)
       return self   
   def __and__(self, other):
       a = posit_2(0, self.x)
       a.v = self.v.__and__(other.v, self.x)
       return a
   def __xor__(self, other):
       a = posit_2(0, self.x)
       a.v = self.v.__xor__(other.v, self.x)
       return a
   def __or__(self, other):
       a = posit_2(0, self.x)
       a.v = self.v.__or__(other.v, self.x)
       return a
   def fma(self, other1, other2):
       try:   
          a = posit_2(0, self.x)
          if isinstance(other1, (int)):
              if isinstance(other2, (int)):
                  a.v = _softposit.pX2_mulAdd(_softposit.i64_to_pX2(other1, self.x), _softposit.i64_to_pX2(other2, self.x), self.v, self.x)
              elif isinstance(other2, (float)):
                  a.v = _softposit.pX2_mulAdd(_softposit.i64_to_pX2(other1, self.x), _softposit.convertDoubleToPX2(other2, self.x), self.v, self.x)
              else:
                  a.v = _softposit.pX2_mulAdd(_softposit.i64_to_pX2(other1, self.x), other2.v, self.v, self.x)
          elif isinstance(other1, (float)):
              if isinstance(other2, (int)):
                  a.v = _softposit.pX2_mulAdd(_softposit.convertDoubleToPX2(other1, self.x), _softposit.i64_to_pX2(other2, self.x), self.v, self.x)
              elif isinstance(other2, (float)):
                  a.v = _softposit.pX2_mulAdd(_softposit.convertDoubleToPX2(other1, self.x), _softposit.convertDoubleToPX2(other2, self.x), self.v, self.x)
              else:
                  a.v = _softposit.pX2_mulAdd(_softposit.convertDoubleToPX2(other1, self.x), other2.v, self.v, self.x)
          else:
              if isinstance(other2, (int)):
                  a.v = _softposit.pX2_mulAdd(self.v, other1.v, _softposit.i64_to_pX2(other2, self.x), self.v, self.x)
              elif isinstance(other2, (float)):
                  a.v = _softposit.pX2_mulAdd(other1.v, _softposit.convertDoubleToPX2(other2, self.x), self.v, self.x)
              else:
                  a.v = _softposit.pX2_mulAdd(other1.v, other2.v, self.v, self.x)   
          return a
       except TypeError:
          print("TypeError: Unsupported fused operand (fma) among mixed precison posit types")
   def toPosit8(self):
       a = posit8(0)
       a.v = _softposit.pX2_to_p8(self.v)
       return a
   def toPosit16(self):
       a = posit16(0)
       a.v = _softposit.pX2_to_p16(self.v)
       return a
   def toPosit32(self):
       a = posit32(0)
       a.v = _softposit.pX2_to_p32(self.v)
       return a
   def toPosit_2(self, x):
       a = posit_2(0, x)
       a.v = _softposit.pX2_to_pX2(self.v, x)       
       a.x = x
       return a
   def toRInt(self):
       return _softposit.pX2_to_i64(self.v)
   def toInt(self):
       return _softposit.pX2_int(self.v)
   def rint(self):
       self.v = _softposit.pX2_roundToInt(self.v, self.x)
       return self
   def sqrt(self):
       a = posit_2(0, self.x)
       a.v = _softposit.pX2_sqrt(self.v, self.x)
       return a
   def __repr__(self):
       a = float(_softposit.convertPX2ToDouble(self.v))
       if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
           return "NaR"
       else:
           return str(a)
   def __str__(self):
       a = float(_softposit.convertPX2ToDouble(self.v))
       if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
           return "NaR"
       else:
           return str(a)
   def __int__(self):
       return _softposit.pX2_int(self.v)
   def __float__(self):
       return float(_softposit.convertPX2ToDouble(self.v))
   def isNaR(self):
       return self.v.isNaR();
   def toNaR(self):
       a = posit_2(0)
       a.v.toNaR();
       return a
   def fromBits(self, value):
       self.v.fromBits(value)
   def toBinary(self):
       self.v.toBits(self.x)
   def toBinaryFormatted(self):
       print(convertToColor((self.v.v)>>(32-self.x), self.x, 2))
   def toHex(self):
       self.v.toHex(self.x)

class quire_2:
   def __init__(self, x):
       self.v = _softposit.qX2Clr();
       self.x = x
   def type(self):
       return 'quire' + str(self.x) +'_2'
   def qma(self, other1, other2):
       try:
          a = posit_2(0, self.x)
          if isinstance(other1, (int)):
              if isinstance(other2, (int)):
                  self.v = _softposit.qX2_fdp_add(self.v, _softposit.i64_to_pX2(other1, self.x), _softposit.i64_to_pX2(other2, self.x))
              elif isinstance(other2, (float)):
                  self.v = _softposit.qX2_fdp_add(self.v, _softposit.i64_to_pX2(other1, self.x), _softposit.convertDoubleToPX2(other2, self.x))
              else:
                  self.v = _softposit.qX2_fdp_add(self.v, _softposit.i64_to_pX2(other1, self.x), other2.v)
          elif isinstance(other1, (float)):
              if isinstance(other2, (int)):
                  self.v = _softposit.qX2_fdp_add(self.v, _softposit.convertDoubleToPX2(other1, self.x), _softposit.i64_to_pX2(other2, self.x))
              elif isinstance(other2, (float)):
                  self.v = _softposit.qX2_fdp_add(self.v, _softposit.convertDoubleToPX2(other1, self.x), _softposit.convertDoubleToPX2(other2, self.x))
              else:
                  self.v = _softposit.qX2_fdp_add(self.v, _softposit.convertDoubleToPX2(other1, self.x), other2.v)
          else:
              if isinstance(other2, (int)):
                  self.v = _softposit.qX2_fdp_add(self.v, other1.v, _softposit.i64_to_pX2(other2, self.x))
              elif isinstance(other2, (float)):
                  self.v = _softposit.qX2_fdp_add(self.v, other1.v, _softposit.convertDoubleToPX2(other2, self.x))
              else:
                  self.v = _softposit.qX2_fdp_add(self.v, other1.v, other2.v)
          return self
       except TypeError:
          print("TypeError: Unsupported fused operand (qma) between quire"+self.x+"_2 and non-posit8 types")     
   def qms(self, other1, other2):
       try:
          a = posit_2(0, self.x)
          if isinstance(other1, (int)):
              if isinstance(other2, (int)):
                  self.v = _softposit.qX2_fdp_sub(self.v, _softposit.i64_to_pX2(other1, self.x), _softposit.i64_to_pX2(other2, self.x))
              elif isinstance(other2, (float)):
                  self.v = _softposit.qX2_fdp_sub(self.v, _softposit.i64_to_pX2(other1, self.x), _softposit.convertDoubleToPX2(other2, self.x))
              else:
                  self.v = _softposit.qX2_fdp_sub(self.v, _softposit.i64_to_pX2(other1, self.x), other2.v)
          elif isinstance(other1, (float)):
              if isinstance(other2, (int)):
                  self.v = _softposit.qX2_fdp_sub(self.v, _softposit.convertDoubleToPX2(other1, self.x), _softposit.i64_to_pX2(other2, self.x))
              elif isinstance(other2, (float)):
                  self.v = _softposit.qX2_fdp_sub(self.v, _softposit.convertDoubleToPX2(other1, self.x), _softposit.convertDoubleToPX2(other2, self.x))
              else:
                  self.v = _softposit.qX2_fdp_sub(self.v, _softposit.convertDoubleToPX2(other1, self.x), other2.v)
          else:
              if isinstance(other2, (int)):
                  self.v = _softposit.qX2_fdp_sub(self.v, other1.v, _softposit.i64_to_pX2(other2, self.x))
              elif isinstance(other2, (float)):
                  self.v = _softposit.qX2_fdp_sub(self.v, other1.v, _softposit.convertDoubleToPX2(other2, self.x))
              else:
                  self.v = _softposit.qX2_fdp_sub(self.v, other1.v, other2.v)
          return self
       except TypeError:
          print("TypeError: Unsupported fused operand (qms) between quire"+x+"_2 and non-posit_2 types")    
   def toPosit(self): 
       a = posit_2(0, self.x)      
       a.v = _softposit.qX2_to_pX2(self.v, self.x);
       return a
   def clr(self):       
       self.v = _softposit.qX2Clr();
   def isNaR(self):       
       return self.v.isNaR();
   def __repr__(self):
       a = float(_softposit.convertPX2ToDouble(_softposit.qX2_to_pX2(self.v, self.x)))
       if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
           return "NaR"
       else:
           return str(a)
   def __str__(self):
       a = float(_softposit.convertPX2ToDouble(_softposit.qX2_to_pX2(self.v, self.x)))
       if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
           return "NaR"
       else:
           return str(a)
   def toBinary(self):
       self.v.toBits()
   def toHex(self):
       self.v.toHex()

%}


%extend posit8_t {
    posit8_t init() {
        posit8_t a;
        return a;
    }
    void fromBits(int bits) {
        $self->v = bits & 0xFF;
    }
    void toBits() {
	printBinary(((uint64_t*)&$self->v), 8);
    }
    void toHex() {
	printHex($self->v);
    }
    int toInt() {
	return $self->v;
    }
    posit8_t __rshift__(int n) {
       posit8_t a; 
       a.v = ($self->v >> n);
       return a;
    }
    posit8_t __lshift__(int n) {
       posit8_t a; 
       a.v = ($self->v << n) & 0xFF;
       return a;
    }
    posit8_t __invert__() {
       $self->v = ~($self->v) & 0xFF;
       return *self;
    }
    posit8_t __neg__() {
       posit8_t a;
       a.v = -($self->v) & 0xFF;
       return a;
    }
    posit8_t __abs__() {
       posit8_t a;
       if (($self->v)>>7) a.v = -($self->v) & 0xFF;
       else a.v = $self->v;
       return a;
    }
    posit8_t __and__(posit8_t other ){
	posit8_t a;
        a.v = $self->v & other.v;
        return a;
    }
    posit8_t __xor__(posit8_t other ){
	posit8_t a;
        a.v = $self->v ^ other.v;
        return a;
    }
    posit8_t __or__(posit8_t other ){
	posit8_t a;
        a.v = $self->v | other.v;
        return a;
    }
    bool isNaR(){
        return $self->v == (uint8_t) 0x80;
    }
    posit8_t toNaR(){        
        $self->v = (uint8_t) 0x80;
        return *$self;
    }
    %pythoncode %{
       def __repr__(self):
           a = float(_softposit.convertP8ToDouble(self))
           if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
               return "NaR"
           else:
               return str(a)
       def __str__(self):
           a = float(_softposit.convertP8ToDouble(self))
           if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
               return "NaR"
           else:
               return str(a)
    %}
};

%extend quire8_t {
    quire8_t(){
        quire8_t* q;
        q->v = 0;
        return q;
    }
    void toBits() {
	printBinary(((uint64_t*)&$self->v), 32);
    }
    void toHex() {
	printHex($self->v);
    }
    quire8_t clr(quire8_t q) {
        $self = &q;
        $self->v = 0;
        return *self;
    }
    bool isNaR(){
        return $self->v == (uint32_t) 0x80000000;
    }
    %pythoncode %{
       def __repr__(self):
           a = float(_softposit.convertP8ToDouble(_softposit.q8_to_p8(self)))
           if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
               return "NaR"
           else:
               return str(a)
       def __str__(self):
           a = float(_softposit.convertP8ToDouble(_softposit.q8_to_p8(self)))
           if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
               return "NaR"
           else:
               return str(a)
    %}
};

%extend posit16_t {
    void fromBits(int bits) {
        $self->v = bits & 0xFFFF;
    }
    void toBits() {
	printBinary(((uint64_t*)&$self->v), 16);
    }
    void toHex() {
	printHex($self->v);
    }
    int toInt() {
	return $self->v;
    }
    posit16_t __rshift__(int n) {
       posit16_t a; 
       a.v = ($self->v >> n);
       return a;
    }
    posit16_t __lshift__(int n) {
       posit16_t a; 
       a.v = ($self->v << n) & 0xFFFF;
       return a;
    }
    posit16_t __invert__() {
       $self->v = ~($self->v) & 0xFFFF;
       return *self;
    }
    posit16_t __neg__() {
       posit16_t a;
       a.v = -($self->v) & 0xFFFF;
       return a;
    }
    posit16_t __abs__() {
       posit16_t a;
       if (($self->v)>>15) a.v = -($self->v) & 0xFFFF;
       else a.v = $self->v;
       return a;
    }
    posit16_t __and__(posit16_t other ){
	posit16_t a;
        a.v = $self->v & other.v;
        return a;
    }
    posit16_t __xor__(posit16_t other ){
	posit16_t a;
        a.v = $self->v ^ other.v;
        return a;
    }
    posit16_t __or__(posit16_t other ){
	posit16_t a;
        a.v = $self->v | other.v;
        return a;
    }
    bool isNaR(){
        return $self->v == (uint16_t) 0x8000;
    }
    posit16_t toNaR(){        
        $self->v = (uint16_t) 0x8000;
        return *$self;
    }
    %pythoncode %{
       def __repr__(self):
           a = float(_softposit.convertP16ToDouble(self))
           if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
               return "NaR"
           else:
               return str(a)
       def __str__(self):
           a = float(_softposit.convertP16ToDouble(self))
           if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
               return "NaR"
           else:
               return str(a)
    %}
	
};

%extend quire16_t {
    quire16_t(){
        quire16_t* q;
        q->v[0] = 0;
        q->v[1] = 0;
        return q;
    }
    void toBits() {
	printBinary(((uint64_t*)&$self->v[0]), 64);
        printBinary(((uint64_t*)&$self->v[1]), 64);
    }
    void toHex() {
	printHex64($self->v[0]);
        printHex64($self->v[1]);
    }
    quire16_t clr(quire16_t q) {
        $self = &q;
        $self->v[0] = 0;
        $self->v[1] = 0;
        return *self;
    }
    bool isNaR(){
        return ($self->v[0] == (uint64_t) 0x8000000000000000) && ($self->v[1] == (uint32_t) 0x0);
    }
    %pythoncode %{
       def __repr__(self):
           a = float(_softposit.convertP16ToDouble(_softposit.q16_to_p16(self)))
           if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
               return "NaR"
           else:
               return str(a)
       def __str__(self):
           a = float(_softposit.convertP16ToDouble(_softposit.q16_to_p16(self)))
           if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
               return "NaR"
           else:
               return str(a)
    %}
};

%extend posit32_t {
    void fromBits(long long int bits) {
        $self->v = bits & 0xFFFFFFFF;
    }
    void toBits() {
	printBinary(((uint64_t*)&$self->v), 32);
    }
    void toHex() {
	printHex($self->v);
    }
    int toInt() {
	return $self->v;
    }
    posit32_t __rshift__(int n) {
       posit32_t a; 
       a.v = ($self->v >> n);
       return a;
    }
    posit32_t __lshift__(int n) {
       posit32_t a; 
       a.v = ($self->v << n) & 0xFFFFFFFF;
       return a;
    }
    posit32_t __invert__() {
       $self->v = ~($self->v) & 0xFFFFFFFF;
       return *self;
    }
    posit32_t __neg__() {
       posit32_t a;
       a.v = -($self->v) & 0xFFFFFFFF;
       return a;
    }
    posit32_t __abs__() {
       posit32_t a;
       if (($self->v)>>31) a.v = -($self->v) & 0xFFFFFFFF;
       else a.v = $self->v;
       return a;
    }
    posit32_t __and__(posit32_t other ){
	posit32_t a;
        a.v = $self->v & other.v;
        return a;
    }
    posit32_t __xor__(posit32_t other ){
	posit32_t a;
        a.v = $self->v ^ other.v;
        return a;
    }
    posit32_t __or__(posit32_t other ){
	posit32_t a;
        a.v = $self->v | other.v;
        return a;
    }
    bool isNaR(){
        return $self->v == (uint32_t) 0x80000000;
    }
    posit32_t toNaR(){        
        $self->v = (uint32_t) 0x80000000;
        return *$self;
    }
    %pythoncode %{
       def __repr__(self):
           a = float(_softposit.convertP32ToDouble(self))
           if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
               return "NaR"
           else:
               return str(a)
       def __str__(self):
           a = float(_softposit.convertP32ToDouble(self))
           if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
               return "NaR"
           else:
               return str(a)
    %}
};

%extend quire32_t {
    quire32_t(){
        quire32_t* q;
        q->v[0] = 0;
        q->v[1] = 0;
        q->v[2] = 0;
        q->v[3] = 0;
        q->v[4] = 0;
        q->v[5] = 0;
        q->v[6] = 0;
        q->v[7] = 0;
        return q;
    }
    void toBits() {
		printBinary(((uint64_t*)&$self->v[0]), 64);
        printBinary(((uint64_t*)&$self->v[1]), 64);
		printBinary(((uint64_t*)&$self->v[2]), 64);
        printBinary(((uint64_t*)&$self->v[3]), 64);
		printBinary(((uint64_t*)&$self->v[4]), 64);
        printBinary(((uint64_t*)&$self->v[5]), 64);
		printBinary(((uint64_t*)&$self->v[6]), 64);
        printBinary(((uint64_t*)&$self->v[7]), 64);
    }
    void toHex() {
		printHex64($self->v[0]);
        printHex64($self->v[1]);
		printHex64($self->v[2]);
        printHex64($self->v[3]);
		printHex64($self->v[4]);
        printHex64($self->v[5]);
		printHex64($self->v[6]);
        printHex64($self->v[7]);
    }
    quire32_t clr(quire32_t q) {
        $self = &q;
        $self->v[0] = 0;
        $self->v[1] = 0;
        $self->v[2] = 0;
        $self->v[3] = 0;
        $self->v[4] = 0;
        $self->v[5] = 0;
        $self->v[6] = 0;
        $self->v[7] = 0;
        return *self;
    }
    bool isNaR(){
        return ($self->v[0] == (uint64_t) 0x8000000000000000) && 
			($self->v[1] == (uint32_t) 0x0) && 
			($self->v[2] == (uint32_t) 0x0) && 
			($self->v[3] == (uint32_t) 0x0) && 
			($self->v[4] == (uint32_t) 0x0) && 
			($self->v[5] == (uint32_t) 0x0) && 
			($self->v[6] == (uint32_t) 0x0) && 
			($self->v[7] == (uint32_t) 0x0);
    }
    %pythoncode %{
       def __repr__(self):
           a = float(_softposit.convertP32ToDouble(_softposit.q32_to_p32(self)))
           if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
               return "NaR"
           else:
               return str(a)
       def __str__(self):
           a = float(_softposit.convertP32ToDouble(_softposit.q32_to_p32(self)))
           if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
               return "NaR"
           else:
               return str(a)
    %}
};

%extend posit_2_t {
    posit_2_t init() {
        posit_2_t a;
        return a;
    }
    void fromBits(long long int bits) {
        $self->v = bits & 0xFFFFFFFF;
    }
    void toBits(int x) {
		printBinaryPX(((uint32_t*)&$self->v), x);
    }
    void toHex(int x) {
		printHexPX($self->v, x);
    }
    int toInt() {
		return $self->v;
    }
    posit_2_t __rshift__(int n, int x) {
       posit_2_t a; 
       a.v = ($self->v >> n) & ((int32_t)0x80000000>>(x-1));
       return a;
    }
    posit_2_t __lshift__(int n, int x) {
       posit_2_t a; 
       a.v = ($self->v << n) & 0xFFFFFFFF;
       return a;
    }
    posit_2_t __invert__(int x) {
       $self->v = (~($self->v))  & ((int32_t)0x80000000>>(x-1));
       return *self;
    }
    posit_2_t __neg__(int x) {
       posit_2_t a;
       a.v = -($self->v) & ((int32_t)0x80000000>>(x-1));
       return a;
    }
    posit_2_t __abs__(int x) {
       posit_2_t a;
       if (($self->v)>>31) a.v = -($self->v) & ((int32_t)0x80000000>>(x-1));
       else a.v = ($self->v) & ((int32_t)0x80000000>>(x-1));
       return a;
    }
    posit_2_t __and__(posit_2_t other, int x){
	posit_2_t a;
        a.v = $self->v & other.v;
        return a;
    }
    posit_2_t __xor__(posit_2_t other, int x ){
	posit_2_t a;
        a.v = ($self->v ^ other.v) & ((int32_t)0x80000000>>(x-1));
        return a;
    }
    posit_2_t __or__(posit_2_t other, int x ){
	posit_2_t a;
        a.v = ($self->v | other.v) & ((int32_t)0x80000000>>(x-1));
        return a;
    }
    bool isNaR(){
        return $self->v == (uint32_t) 0x80000000;
    }
    posit_2_t toNaR(){        
        $self->v = (uint32_t) 0x80000000;
        return *$self;
    }
    %pythoncode %{
       def __repr__(self):
           a = float(_softposit.convertPX2ToDouble(self))
           if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
               return "NaR"
           else:
               return str(a)
       def __str__(self):
           a = float(_softposit.convertPX2ToDouble(self))
           if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
               return "NaR"
           else:
               return str(a)
    %}
};


%extend quire_2_t {
    quire_2_t(){
        quire_2_t* q;
        q->v[0] = 0;
        q->v[1] = 0;
        q->v[2] = 0;
        q->v[3] = 0;
        q->v[4] = 0;
        q->v[5] = 0;
        q->v[6] = 0;
        q->v[7] = 0;
        return q;
    }
    void toBits() {
	printBinary(((uint64_t*)&$self->v[0]), 64);
        printBinary(((uint64_t*)&$self->v[1]), 64);
	printBinary(((uint64_t*)&$self->v[2]), 64);
        printBinary(((uint64_t*)&$self->v[3]), 64);
	printBinary(((uint64_t*)&$self->v[4]), 64);
        printBinary(((uint64_t*)&$self->v[5]), 64);
	printBinary(((uint64_t*)&$self->v[6]), 64);
        printBinary(((uint64_t*)&$self->v[7]), 64);
    }
    void toHex() {
	printHex64($self->v[0]);
        printHex64($self->v[1]);
	printHex64($self->v[2]);
        printHex64($self->v[3]);
	printHex64($self->v[4]);
        printHex64($self->v[5]);
	printHex64($self->v[6]);
        printHex64($self->v[7]);
    }
    quire_2_t clr(quire_2_t q) {
        $self = &q;
        $self->v[0] = 0;
        $self->v[1] = 0;
        $self->v[2] = 0;
        $self->v[3] = 0;
        $self->v[4] = 0;
        $self->v[5] = 0;
        $self->v[6] = 0;
        $self->v[7] = 0;
        return *self;
    }
    bool isNaR(){
        return ($self->v[0] == (uint64_t) 0x8000000000000000) && 
			($self->v[1] == (uint32_t) 0x0) && 
			($self->v[2] == (uint32_t) 0x0) && 
			($self->v[3] == (uint32_t) 0x0) && 
			($self->v[4] == (uint32_t) 0x0) && 
			($self->v[5] == (uint32_t) 0x0) && 
			($self->v[6] == (uint32_t) 0x0) && 
			($self->v[7] == (uint32_t) 0x0);
    }
    %pythoncode %{
       def __repr__(self, x):
           a = float(_softposit.convertPX2ToDouble(_softposit.qX2_to_pX2(self, x)))
           if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
               return "NaR"
           else:
               return str(a)
       def __str__(self, x):
           a = float(_softposit.convertPX2ToDouble(_softposit.qX2_to_pX2(self, x)))
           if (a == float('inf')) or (a==float('-inf')) or (a==float('nan')):
               return "NaR"
           else:
               return str(a)
    %}
};


