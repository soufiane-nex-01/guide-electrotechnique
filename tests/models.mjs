import fs from 'node:fs/promises';import vm from 'node:vm';import assert from 'node:assert/strict';
const w={window:{},document:{querySelector:()=>null},localStorage:{getItem:()=>null},Intl};vm.createContext(w);
for(const f of ['electro-models','exams'])vm.runInContext(await fs.readFile(new URL('../desktop/app/'+f+'.js', import.meta.url),'utf8'),w);
const m=w.window.ElectroModels,exam=w.window.GuideExam;let count=0;
const near=(a,b,e=1e-9)=>{assert.ok(Math.abs(a-b)<e,`${a} != ${b}`);count++;};
near(m.divider(12,1000,2000).vout,8);near(m.divider(12,1000,2000).i,.004);
near(m.divider(12,1000,2000,'open').i,0);near(m.divider(12,1000,2000,'open').vout,12);near(m.divider(12,1000,2000,'short').vout,0);near(m.divider(12,1000,2000,'short').i,.012);
near(m.bridge(12,1000,500,2000,1000).difference,0);near(m.bridge(12,1000,1000,2000,1000).difference,2);
near(m.rc(12,2000,.0005,1).tau,1);near(m.rc(12,2000,.0005,1).v,12*(1-Math.exp(-1)));near(m.rc(12,2000,.0005,0,true).v,12);near(m.rc(12,2000,.0005,0).v,0);
near(m.ac(24,2,60).p,24);near(m.ac(24,2,60).q,24*Math.sqrt(3));near(m.ac(24,2,-60).q,-24*Math.sqrt(3));assert.equal(m.ac(0,2,45).pf,null);
for(const p of [0,2.5,5,7.5,10]){const v=m.sensor(p,0,10);near(v.ma,4+1.6*p);near(v.decoded,p);}assert.equal(m.sensor(5,0,10,true).valid,false);assert.equal(m.sensor(5,0,10,true).decoded,null);near(m.sensor(10,0,10).adc,4095);
near(m.pwm(24,.25,100).v,6);near(m.pwm(24,0,100).p,0);near(m.pwm(24,1,100).p,5.76);
for(const previous of [false,true])for(const start of [false,true])for(const stop of [false,true])for(const trip of [false,true]){assert.equal(m.latch(previous,start,stop,trip),!stop&&!trip&&(start||previous));count++;}
for(const call of [()=>m.rc(12,0,1,1),()=>m.sensor(2,0,1),()=>m.divider(NaN,1,1),()=>m.pwm(12,1.1,20),()=>m.bridge(12,1,0,1,1),()=>m.ac(24,2,100)]){assert.throws(call);count++;}
for(const seed of [1,3,100,987654321,2147483647]){const qs=exam.questions(seed);assert.equal(qs.length,12);for(const q of qs){assert.equal(exam.grade(q,String(q.answer)),true);assert.equal(exam.grade(q,''),false);assert.equal(exam.grade(q,'Infinity'),false);count+=3;if(q.type==='number'){assert.equal(exam.grade(q,String(q.answer*1.1)),false);assert.equal(exam.grade(q,String(q.answer*1.01)),true);count+=2;}}}
near(exam.parse('١٢٫٥'),12.5);near(exam.parse('۱۲,۵'),12.5);assert.ok(Number.isNaN(exam.parse('12abc')));assert.ok(Number.isNaN(exam.parse('')));
console.log(JSON.stringify({checks:count,status:'pass',models:7,assessmentSeeds:5}));
