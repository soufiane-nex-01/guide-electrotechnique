/* Pure, bounded ideal models. SI units except sensor current (mA). */
(() => {
 'use strict';
 const check=(...v)=>{if(v.some(x=>!Number.isFinite(x)))throw Error('finite');};
 function divider(u,r1,r2,fault='none'){check(u,r1,r2);if(u<0||r1<=0||r2<=0)throw Error('range');const i=fault==='open'?0:u/(r1+(fault==='short'?0:r2));return {i,vout:fault==='open'?u:fault==='short'?0:i*r2,p:u*i};}
 function rc(u,r,c,time,discharge=false){check(u,r,c,time);if(u<0||r<=0||c<=0||time<0)throw Error('range');const tau=r*c,a=Math.exp(-time/tau);return {tau,v:u*(discharge?a:1-a),i:(discharge?-1:1)*u*a/r};}
 function ac(u,i,phi){check(u,i,phi);if(u<0||i<0||Math.abs(phi)>90)throw Error('range');const rad=phi*Math.PI/180,s=u*i;return {s,p:s*Math.cos(rad),q:s*Math.sin(rad),pf:s?Math.cos(rad):null};}
 function sensor(value,min,max,open=false){check(value,min,max);if(max<=min||value<min||value>max)throw Error('range');const ma=open?0:4+16*(value-min)/(max-min),volts=ma*.25;return {ma,volts,adc:Math.round(volts/5*4095),valid:!open,decoded:open?null:min+(ma-4)/16*(max-min)};}
 function pwm(u,duty,r){check(u,duty,r);if(u<0||duty<0||duty>1||r<=0)throw Error('range');const v=u*duty;return {v,i:v/r,p:v*v/r};}
 function bridge(u,r1,r2,r3,r4){check(u,r1,r2,r3,r4);if(u<0||Math.min(r1,r2,r3,r4)<=0)throw Error('range');const left=u*r2/(r1+r2),right=u*r4/(r3+r4);return {left,right,difference:left-right};}
 function latch(previous,start,stop,trip){return !stop&&!trip&&(start||previous);}
 window.ElectroModels=Object.freeze({divider,rc,ac,sensor,pwm,bridge,latch});
})();
