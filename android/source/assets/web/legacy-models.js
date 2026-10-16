/* Numeric models for native reconstructions of the original interactive lessons. */
(() => {
 'use strict';
 const finite=(...a)=>{if(a.some(v=>!Number.isFinite(v)))throw Error('finite');};
 function wire(target,{rho,length,section,resistance}){const v={rho,length,section,resistance};const required=Object.keys(v).filter(k=>k!==target);finite(...required.map(k=>v[k]));if(required.some(k=>v[k]<=0))throw Error('positive');const result=target==='resistance'?rho*length/section:target==='section'?rho*length/resistance:target==='length'?resistance*section/rho:target==='rho'?resistance*section/length:NaN;finite(result);return result;}
 function temperature(r20,alpha0,theta){finite(r20,alpha0,theta);if(r20<0||1+alpha0*20<=0||1+alpha0*theta<0)throw Error('range');return r20*(1+alpha0*theta)/(1+alpha0*20);}
 function generator(e,r,load){finite(e,r,load);if(e<0||r<0||load<0||r+load===0)throw Error('range');const i=e/(r+load);return {i,u:i*load,drop:i*r,loss:r*i*i,p:load*i*i};}
 function active(e,r,em,rm,rho,length,section){finite(e,r,em,rm,rho,length,section);if(Math.min(e,r,em,rm,rho,length)<0||section<=0)throw Error('range');const rl=rho*length/section,total=r+rm+rl;if(total<=0)throw Error('range');const i=(e-em)/total;return {i,rl,ug:e-r*i,um:em+rm*i,lineDrop:rl*i,sourceDrop:r*i,receiverDrop:rm*i};}
 function rlc(u,f,r,l,c){finite(u,f,r,l,c);if(Math.min(u,r,l,c)<0||f<=0)throw Error('range');const xl=2*Math.PI*f*l,xc=c>0?1/(2*Math.PI*f*c):0,x=xl-xc,z=Math.hypot(r,x);if(z<1e-12)throw Error('short');const i=u/z,phi=Math.atan2(x,r);return {xl,xc,x,z,i,phi:phi*180/Math.PI,pf:r/z,ur:i*r,ul:i*xl,uc:i*xc,p:i*i*r,q:i*i*x,s:u*i};}
 function boucherot(loads,u,f,targetPF=Math.sqrt(1/1.16),three=false,coupling='delta',compensate=true){finite(u,f,targetPF);if(u<=0||f<=0||targetPF<=0||targetPF>1||loads.length>8)throw Error('range');let p=0,q=0;
  for(const load of loads){finite(load.useful,load.efficiency,load.pf);if(load.useful<0||load.efficiency<=0||load.efficiency>100||load.pf<=0||load.pf>1)throw Error('load');const active=load.useful*100/load.efficiency;p+=active;q+=active*Math.tan(Math.acos(load.pf))*(load.capacitive?-1:1);}
  const s=Math.hypot(p,q),den=u*(three?Math.sqrt(3):1),qc=compensate?Math.max(0,q-p*Math.tan(Math.acos(targetPF))):0,afterQ=q-qc,afterS=Math.hypot(p,afterQ),capacitor=qc/(2*Math.PI*f*u*u*(three&&coupling==='delta'?3:1));
  return {p,q,s,i:s/den,pf:s?p/s:null,qc,capacitor,afterQ,afterS,afterI:afterS/den,afterPF:afterS?p/afterS:null};
 }
 function transformer(target,{u1,u2,n1,n2}){const v={u1,u2,n1,n2},required=Object.keys(v).filter(k=>k!==target);finite(...required.map(k=>v[k]));if(required.some(k=>v[k]<=0))throw Error('positive');return target==='u1'?u2*n1/n2:target==='u2'?u1*n2/n1:target==='n1'?n2*u1/u2:target==='n2'?n1*u2/u1:NaN;}
 function resistor(d1,d2,multiplier,tolerance){finite(d1,d2,multiplier,tolerance);if(!Number.isInteger(d1)||!Number.isInteger(d2)||d1<1||d1>9||d2<0||d2>9||multiplier< -2||multiplier>9||tolerance<=0)throw Error('range');const value=(10*d1+d2)*10**multiplier;return {value,min:value*(1-tolerance/100),max:value*(1+tolerance/100)};}
 function cylinder(pressure,diameter,rod){finite(pressure,diameter,rod);if(pressure<0||diameter<=0||rod<0||rod>=diameter)throw Error('range');const area=Math.PI*(diameter/1000)**2/4,rodArea=Math.PI*(rod/1000)**2/4;return {area,extension:pressure*1e5*area,retraction:pressure*1e5*(area-rodArea)};}
 function timer(mode,input,t,delay){finite(t,delay);if(t<0||delay<0)throw Error('range');return mode==='on'?input&&t>=delay:input||t<delay;}
 function lighting(mode,a,b,memory=false){if(mode==='double')return [!!a,!!b];if(mode==='two-way')return [a===b];if(mode==='latching')return [!!memory];return [!!a];}
 function heating(rooms,h,tin,tex){finite(h,tin,tex);if(h<=0||h>3.5||tin>24||tex< -27||tin<tex||rooms.length>10)throw Error('range');const coefficient=10+.8*(tin-tex);let surface=0,power=0,installed=0;const rows=rooms.map(({length,width})=>{finite(length,width);if(length<0||width<0||length*width>50)throw Error('room');const area=length*width,p=area*h*coefficient;if(p>=9000)throw Error('room');const heater=p<250?0:(Math.floor(p/250)+1)*250;surface+=area;power+=p;installed+=heater;return {area,power:p,installed:heater};});return {surface,volume:surface*h,power,installed,rows};}
 // Educational state table for three equal heater banks; not recovered wiring.
 function cam(position){if(!Number.isInteger(position)||position<0||position>3)throw Error('range');return [0,1,2].map(i=>i<position);}
 window.LegacyModels=Object.freeze({wire,temperature,generator,active,rlc,boucherot,transformer,resistor,cylinder,timer,lighting,heating,cam});
})();
