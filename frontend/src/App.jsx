import React, { useState, useEffect, useRef } from 'react'
import {
  ArrowLeftRight, Menu, X, Home, MessageCircle, Users, Settings, CircleHelp,
  LogOut, Search, Send, Paperclip, Smile, Music2, Code2,
  Laptop, Palette, Check, Clock3, ChevronRight, UserRound, Bell, Plus, Zap, Heart,
  CalendarDays, BookOpen, ArrowUpRight, ShieldCheck, Mail, Lock, Sparkles
} from 'lucide-react'

const chats = [
  { name:'Anita Desai', initials:'AD', skill:'React ⇄ Figma', online:true, messages:[['them','Hey! Great to connect. What would you like to start with?'],['you','Hi Anita! I would love to learn Figma basics. I can help you with React.'],['them','Perfect! Let’s plan a first session this week.']] },
  { name:'Karthik Raj', initials:'KR', skill:'UI Design ⇄ React', online:true, messages:[['them','I checked out your profile. Your skill goals look great!'],['you','Thanks! Excited to exchange knowledge.']] },
  { name:'Meera Nair', initials:'MN', skill:'Python ⇄ Illustration', online:false, messages:[['them','Would Saturday afternoon work for you?']] },
]
const offers = [
  { name:'Anita Desai', initials:'AD', status:'ACTIVE', teach:'React', learn:'Figma', date:'Started 2 days ago' },
  { name:'Karthik Raj', initials:'KR', status:'ACTIVE', teach:'UI Design', learn:'JavaScript', date:'Started 5 days ago' },
  { name:'Meera Nair', initials:'MN', status:'ACTIVE', teach:'Python', learn:'Illustration', date:'Started yesterday' },
]
const Button = ({children, onClick, primary=false, className=''}) => <button onClick={onClick} className={`press inline-flex items-center justify-center gap-2 border-2 border-black px-4 py-2.5 text-sm font-extrabold uppercase tracking-wide ${primary?'bg-blue-600 text-white hard-sm':'bg-white text-black hard-sm'} ${className}`}>{children}</button>
const Tag = ({children, blue=false}) => <span className={`mono inline-flex items-center border-2 border-black px-2 py-1 text-[10px] font-bold uppercase tracking-wide ${blue?'bg-blue-100':'bg-white'}`}>{children}</span>
const Avatar = ({initials, color='bg-blue-200', size='h-12 w-12'}) => <div className={`${size} ${color} flex shrink-0 items-center justify-center border-2 border-black font-display text-lg font-black`}>{initials}</div>

function Logo({small=false}) {
  return <div className="flex items-center gap-2"><span className="flex h-10 w-10 items-center justify-center border-2 border-black bg-blue-600 text-white"><ArrowLeftRight size={22}/></span><span className={`font-display font-black tracking-tight ${small?'text-xl':'text-2xl'}`}>SkillSwap</span></div>
}
function Login({onLogin,onSignup}) {
  const [email,setEmail]=useState('')
  const [password,setPassword]=useState('')
  const [name,setName]=useState('')
  const [bio,setBio]=useState('')
  const [mode,setMode]=useState('login')
  const [loading,setLoading]=useState(false)
  const [error,setError]=useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const url = mode === 'register'
        ? 'http://localhost:8080/api/auth/register'
        : 'http://localhost:8080/api/auth/login'
      const body = mode === 'register'
        ? {name, email, password, bio}
        : {email, password}
      const res = await fetch(url, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(body)
      })
      const data = await res.json()
      if (!res.ok) throw new Error(data.message || (mode === 'register' ? 'Registration failed' : 'Login failed'))
      onLogin(data.name, data.email, data.id || null, data.bio || '')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-blue-50 px-4 py-12">
      <div className="w-full max-w-md border-2 border-black bg-white p-7 shadow-[6px_6px_0_#000] sm:p-9">
        <div className="mb-8 flex flex-col items-center text-center"><Logo/><p className="mono mt-3 text-[10px] font-bold tracking-[.2em]">NEVER STOP LEARNING.</p></div>
        <h1 className="mb-1 text-3xl font-black tracking-tight">{mode === 'login' ? 'Welcome back.' : 'Create Account'}</h1>
        <p className="mb-6 text-sm text-gray-600">{mode === 'login' ? 'Log in and pick up where your learning left off.' : 'Join the SkillSwap community.'}</p>
        <form onSubmit={handleSubmit} className="space-y-4">
          {mode === 'register' && (
            <>
              <label className="block text-sm font-bold">Full Name<input required type="text" value={name} onChange={e=>setName(e.target.value)} placeholder="Rohan" className="field mt-2"/></label>
              <label className="block text-sm font-bold">Bio<input type="text" value={bio} onChange={e=>setBio(e.target.value)} placeholder="Tell us about yourself" className="field mt-2"/></label>
            </>
          )}
          <label className="block text-sm font-bold">Email address<input required type="email" value={email} onChange={e=>setEmail(e.target.value)} placeholder="you@example.com" className="field mt-2"/></label>
          <label className="block text-sm font-bold">Password<input required minLength={mode === 'register' ? 6 : 4} type="password" value={password} onChange={e=>setPassword(e.target.value)} placeholder={mode === 'register' ? 'Create a password' : 'Enter your password'} className="field mt-2"/></label>
          {error && <p className="text-[10px] text-red-600 font-bold">{error}</p>}
          <Button primary className="w-full py-3" disabled={loading}>
            {loading ? 'Processing...' : (mode === 'login' ? 'Login <ArrowUpRight size={16}/>' : 'Create Account')}
          </Button>
        </form>
        <div className="my-5 flex items-center gap-3"><div className="h-[2px] flex-1 bg-black"/><span className="mono text-[10px] font-bold">{mode === 'login' ? 'NEW HERE?' : 'ALREADY HAVE AN ACCOUNT?'}</span><div className="h-[2px] flex-1 bg-black"/></div>
        <Button onClick={()=>setMode(mode === 'login' ? 'register' : 'login')} className="w-full py-3">
          {mode === 'login' ? 'Sign Up' : 'Sign In'}
        </Button>
        <p className="mono mt-6 text-center text-[10px] text-gray-500">A COMMUNITY BUILT ON SHARED KNOWLEDGE.</p>
      </div>
    </div>
  )
}
function Sidebar({view,setView,logout}) {
  const nav=[['Dashboard',Home,'dashboard'],['Messages',MessageCircle,'chat'],['Skill Rooms',Users,'rooms'],['Settings',Settings,'settings'],['Profile',UserRound,'profile'],['FAQ',CircleHelp,'faq']]
  return <aside className="h-fit border-2 border-black bg-white p-3 shadow-[4px_4px_0_#000]">
    <div className="mb-4 flex items-center gap-2 px-2 py-3"><Logo small/></div>
    <nav className="space-y-1">{nav.map(([label,Icon,id])=><button key={id} onClick={()=>setView(id==='rooms'?'discover':id==='faq'?'dashboard':id)} className={`flex w-full items-center gap-3 border-2 px-3 py-3 text-left text-sm font-bold ${view===id?'border-black bg-blue-600 text-white shadow-[2px_2px_0_#000]':'border-transparent hover:border-black hover:bg-blue-50'}`}><Icon size={18}/>{label}</button>)}</nav>
    <button onClick={logout} className="mt-3 flex w-full items-center gap-3 border-2 border-transparent px-3 py-3 text-sm font-black uppercase hover:border-black hover:bg-blue-50"><LogOut size={18}/> Logout</button>
  </aside>
}
function Hero({setView}) {
  return <section className="grid gap-6 border-2 border-black bg-white p-6 shadow-[6px_6px_0_#000] lg:grid-cols-[1.1fr_.9fr] lg:p-9">
    <div className="flex flex-col items-start justify-center"><Tag blue>LEARN • TEACH • REPEAT</Tag><h1 className="mt-5 max-w-xl text-5xl font-black leading-[.98] tracking-tight sm:text-6xl">Swap Skills,<br/><span className="text-blue-600">Spark Growth.</span></h1><p className="mt-5 max-w-lg text-base leading-7 text-gray-700">Connect with people around the world to teach and learn new skills—no money needed, just passion and knowledge.</p><Button primary onClick={()=>setView('discover')} className="mt-7 px-6 py-4">Join the community <ArrowUpRight size={17}/></Button></div>
    <div className="relative flex min-h-[260px] items-center justify-center overflow-hidden border-2 border-black bg-blue-600 p-5">
      <div className="absolute left-4 top-4 mono text-[10px] font-bold text-white">KNOWLEDGE GOES BOTH WAYS ↗</div>
      <div className="grid w-full max-w-sm grid-cols-2 gap-3">
        <div className="flex min-h-40 flex-col items-center justify-center border-2 border-black bg-white p-4 text-center shadow-[4px_4px_0_#000]"><div className="text-5xl">🎸</div><p className="mt-3 font-black">Acoustic Guitar</p><Tag>CREATIVE</Tag></div>
        <div className="mt-9 flex min-h-40 flex-col items-center justify-center border-2 border-black bg-blue-100 p-4 text-center shadow-[4px_4px_0_#000]"><div className="text-5xl">💻</div><p className="mt-3 font-black">Coding</p><Tag>TECH</Tag></div>
      </div>
      <div className="absolute bottom-3 left-4 flex items-center gap-2 text-white"><ArrowLeftRight/><span className="mono text-[10px] font-bold">SKILLS IN EXCHANGE</span></div>
    </div>
  </section>
}
function Dashboard({setView}) {
  return <div className="space-y-7"><Hero setView={setView}/>
    <div className="grid gap-4 sm:grid-cols-3">{[['Discover','Find your next skill partner.',Search,'discover'],['Your exchanges','Keep learning together.',ArrowLeftRight,'offers'],['Messages','Make plans and share ideas.',MessageCircle,'chat']].map(([title,desc,Icon,id])=><button key={id} onClick={()=>setView(id)} className="press border-2 border-black bg-blue-50 p-5 text-left shadow-[4px_4px_0_#000]"><Icon className="mb-5 text-blue-700" size={25}/><h3 className="text-xl font-black">{title}</h3><p className="mt-1 text-sm text-gray-600">{desc}</p><p className="mono mt-4 text-[10px] font-bold uppercase">Explore <span className="text-blue-700">↗</span></p></button>)}</div>
    <section><div className="mb-4 flex items-end justify-between"><div><p className="mono text-xs font-bold uppercase text-blue-700">Made for your goals</p><h2 className="text-2xl font-black">Recommended for you</h2></div><button onClick={()=>setView('discover')} className="mono text-xs font-bold underline">VIEW ALL</button></div><div className="grid gap-4 md:grid-cols-2"></div></section>
  </div>
}
function PersonCard({user,onOffer,onProfile,userId}) {
  const initials = user.name.split(' ').map(n => n[0]).filter(Boolean).join('').toUpperCase()
  const teachNames = (user.teachSkills || []).map(s => s.name)
  const learnNames = (user.learnSkills || []).map(s => s.name)
  const isSelf = String(user.id) === String(userId)
  return <article className="border-2 border-black bg-white p-5 shadow-[4px_4px_0_#000]">
    <div className="flex flex-wrap items-start justify-between gap-3"><div className="flex items-center gap-3"><Avatar initials={initials} /><div><h3 className="font-black">{user.name}</h3>{user.bio && <p className="text-xs text-gray-600">{user.bio}</p>}</div></div></div>
    <div className="mt-4 space-y-2">
      {teachNames.length > 0 && <div className="flex flex-wrap items-center gap-2"><Tag blue>Can teach: {teachNames.join(', ')}</Tag></div>}
      {learnNames.length > 0 && <div className="flex flex-wrap items-center gap-2"><Tag>Wants to learn: {learnNames.join(', ')}</Tag></div>}
    </div>
    <div className="mt-5 flex flex-wrap gap-2"><Button onClick={()=>onProfile(user)}>View Profile</Button><Button primary onClick={isSelf?undefined:()=>onOffer(user.id)} disabled={isSelf} className={isSelf?'border-2 border-black bg-gray-200 text-gray-500 cursor-not-allowed hover:bg-gray-200 active:bg-gray-200':''} title={isSelf?'You cannot send an offer to yourself.':undefined}>Send Exchange Offer</Button></div>
  </article>
}
function Discover({setView,userId}) {
  const [query,setQuery]=useState('')
  const [users,setUsers]=useState([])
  const [loading,setLoading]=useState(false)
  const [error,setError]=useState('')
  const requestSeq=useRef(0)
  const [debounceTimer,setDebounceTimer]=useState(null)

  const fetchUsers=async(name='')=>{
    const mySeq = ++requestSeq.current
    setLoading(true)
    setError('')
    try{
      const url = name ? `http://localhost:8080/api/users/discover?name=${encodeURIComponent(name)}` : 'http://localhost:8080/api/users/discover'
      const res=await fetch(url)
      if(!res.ok) throw new Error('Failed to load users')
      const data=await res.json()
      if(mySeq !== requestSeq.current) return
      setUsers(data)
    }catch(err){
      if(mySeq !== requestSeq.current) return
      setError(err.message)
      setUsers([])
    }finally{
      if(mySeq !== requestSeq.current) return
      setLoading(false)
    }
  }

  useEffect(()=>{
    fetchUsers()
  },[])

  useEffect(()=>{
    return ()=>{ if(debounceTimer) clearTimeout(debounceTimer) }
  },[])

  const handleSearch=(e)=>{
    const val=e.target.value.trim()
    setQuery(val)
    if(debounceTimer) clearTimeout(debounceTimer)
    const id=setTimeout(()=>{
      fetchUsers(val)
    },300)
    setDebounceTimer(id)
  }

  return <div><div className="mb-6"><p className="mono text-xs font-bold uppercase text-blue-700">Find your people</p><h1 className="text-4xl font-black tracking-tight">Discover partners<span className="text-blue-600">.</span></h1><p className="mt-2 text-gray-600">Find someone whose skills complement yours.</p></div>
    <div className="mb-6 flex flex-col gap-3 border-2 border-black bg-blue-50 p-3 sm:flex-row"><div className="relative flex-1"><Search className="absolute left-3 top-3 text-gray-500" size={18}/><input value={query} onChange={handleSearch} placeholder="Search by name..." className="field pl-10"/></div></div>
    {loading ? (
      <div className="flex items-center justify-center py-10"><p className="mono text-xs font-bold uppercase text-blue-700">Loading users...</p></div>
    ) : error ? (
      <div className="flex flex-col items-center justify-center py-20 border-2 border-black bg-white p-8 shadow-[4px_4px_0_#000]"><p className="mono text-xs font-bold uppercase text-red-600">{error}</p><button onClick={()=>fetchUsers()} className="mt-4 border-2 border-black bg-blue-600 text-white px-6 py-3 font-black uppercase hover:bg-blue-700 active:scale-95">Retry</button></div>
    ) : (
      <>
        <div className="mb-4 flex items-center justify-between"><h2 className="text-xl font-black">{users.length} MEMBERS</h2></div>
        <div className="grid gap-4 xl:grid-cols-2">
          {users.length === 0 ? (
            <div className="border-2 border-dashed border-black bg-blue-50 p-10 text-center"><Search className="mx-auto mb-3"/><h3 className="font-black">No users found.</h3>{query && <p className="mt-1 text-sm text-gray-600">No users found matching your search.</p>}{!query && <p className="mt-1 text-sm text-gray-600">No registered users available.</p>}</div>
          ) : (
            users.map(u=><PersonCard key={u.id} user={u} userId={userId} onOffer={()=>setView('offers')} onProfile={(user)=>alert(`${user.name}\n${user.bio||'No bio available'}`)}/>)
          )}
        </div>
      </>
    )}
  </div>
}
function Offers({setView}) {
  const [tab,setTab]=useState('Active Exchanges (3)')
  const tabs=['Received Offers (2)','Sent Offers (1)','Active Exchanges (3)','Completed (5)']
  const rows=tab.startsWith('Active')?offers:tab.startsWith('Received')?[{...offers[0],status:'NEW OFFER'}]:tab.startsWith('Sent')?[{...offers[1],status:'PENDING'}]:[{...offers[2],status:'COMPLETED'}]
  return <div><p className="mono text-xs font-bold uppercase text-blue-700">Your learning journey</p><h1 className="mb-6 text-4xl font-black tracking-tight">Exchange offers<span className="text-blue-600">.</span></h1>
    <div className="mb-6 flex gap-2 overflow-x-auto pb-3">{tabs.map(t=><button key={t} onClick={()=>setTab(t)} className={`whitespace-nowrap border-2 border-black px-3 py-2 text-xs font-extrabold ${tab===t?'bg-blue-600 text-white shadow-[2px_2px_0_#000]':'bg-white hover:bg-blue-50'}`}>{t}</button>)}</div>
    <div className="space-y-4">{rows.map((o,i)=><article key={o.name} className="border-2 border-black bg-white p-5 shadow-[4px_4px_0_#000]"><div className="flex flex-wrap items-center justify-between gap-3"><div className="flex items-center gap-3"><Avatar initials={o.initials}/><div><h3 className="font-black">{o.name}</h3><p className="text-xs text-gray-500">{o.date}</p></div></div><Tag blue>{o.status}</Tag></div><div className="mt-5 grid items-center gap-3 border-2 border-black bg-blue-50 p-4 sm:grid-cols-[1fr_auto_1fr]"><div><p className="mono text-[10px] font-bold uppercase text-gray-500">You teach</p><p className="mt-1 text-lg font-black"><Code2 className="mr-1 inline text-blue-700"/>{o.teach}</p></div><ArrowLeftRight className="mx-auto text-blue-700" size={25}/><div><p className="mono text-[10px] font-bold uppercase text-gray-500">They teach</p><p className="mt-1 text-lg font-black"><Palette className="mr-1 inline text-blue-700"/>{o.learn}</p></div></div><div className="mt-4 flex flex-wrap gap-2"><Button primary onClick={()=>setView('chat')}><MessageCircle size={15}/> Open Chat</Button><Button onClick={()=>alert(`Exchange with ${o.name}\nYou teach ${o.teach}; they teach ${o.learn}.`)}>View Details</Button>{tab.startsWith('Received')&&<Button onClick={()=>alert('Offer accepted! You can coordinate in Messages.')}>Accept Offer <Check size={15}/></Button>}</div></article>)}</div>
  </div>
}
function Chat({}) {
  const [active,setActive]=useState(0),[text,setText]=useState(''),[search,setSearch]=useState('')
  const [conversations,setConversations]=useState(chats)
  const current=conversations[active]
  const send=()=>{if(!text.trim())return;setConversations(prev=>prev.map((c,i)=>i===active?{...c,messages:[...c.messages,['you',text.trim()]]}:c));setText('')}
  return <div><p className="mono text-xs font-bold uppercase text-blue-700">Keep the exchange going</p><h1 className="mb-5 text-4xl font-black tracking-tight">Messages<span className="text-blue-600">.</span></h1>
    <div className="grid min-h-[580px] overflow-hidden border-2 border-black bg-white shadow-[4px_4px_0_#000] lg:grid-cols-[280px_1fr]">
      <aside className="border-b-2 border-black bg-white lg:border-b-0 lg:border-r-2"><div className="border-b-2 border-black p-4"><h2 className="mb-3 text-xl font-black">Conversations</h2><div className="relative"><Search size={16} className="absolute left-3 top-3 text-gray-500"/><input value={search} onChange={e=>setSearch(e.target.value)} placeholder="Search chats" className="field py-2 pl-9 text-sm"/></div></div><div className="max-h-[430px] overflow-y-auto">{conversations.filter(c=>c.name.toLowerCase().includes(search.toLowerCase())).map(c=>{const i=conversations.indexOf(c);return <button key={c.name} onClick={()=>setActive(i)} className={`flex w-full items-center gap-3 border-b border-black/20 p-4 text-left ${active===i?'bg-blue-100':'hover:bg-blue-50'}`}><Avatar initials={c.initials} size="h-10 w-10"/><div className="min-w-0 flex-1"><div className="flex items-center justify-between gap-2"><span className="font-black">{c.name}</span>{c.online&&<span className="h-2.5 w-2.5 rounded-full border border-black bg-green-400"/>}</div><p className="mono truncate text-[10px] text-gray-500">{c.skill}</p></div></button>})}</div></aside>
      <section className="flex min-h-[580px] flex-col"><header className="flex flex-wrap items-center justify-between gap-3 border-b-2 border-black bg-blue-50 p-4"><div className="flex items-center gap-3"><Avatar initials={current.initials} size="h-10 w-10"/><div><h3 className="font-black">{current.name}</h3><p className="text-xs">{current.online?'● Online':'○ Offline'} · {current.skill}</p></div></div><Tag blue>{current.skill}</Tag></header>
        <div className="flex-1 space-y-4 overflow-y-auto bg-white p-4 sm:p-6">{current.messages.map(([who,msg],i)=><div key={i} className={`flex ${who==='you'?'justify-end':'justify-start'}`}><div className={`max-w-[85%] border-2 border-black px-4 py-3 text-sm leading-6 shadow-[2px_2px_0_#000] ${who==='you'?'bg-blue-600 text-white':'bg-blue-100 text-black'}`}>{msg}<p className={`mono mt-2 text-[9px] ${who==='you'?'text-blue-100':'text-gray-500'}`}>{who==='you'?'YOU':'THEM'} · JUST NOW</p></div></div>)}</div>
        <div className="flex items-center gap-2 border-t-2 border-black bg-white p-3"><button onClick={()=>alert('Attachment picker is a prototype interaction.')} className="border-2 border-black p-2 hover:bg-blue-50" aria-label="Attach file"><Paperclip size={18}/></button><input value={text} onChange={e=>setText(e.target.value)} onKeyDown={e=>e.key==='Enter'&&send()} placeholder="Write a message..." className="field min-w-0 flex-1 py-2"/><button onClick={()=>setText(t=>t+' 😊')} className="hidden border-2 border-black p-2 hover:bg-blue-50 sm:block" aria-label="Add emoji"><Smile size={18}/></button><Button primary onClick={send} className="px-3"><Send size={17}/><span className="hidden sm:inline">Send</span></Button></div>
      </section>
    </div>
  </div>
}
function Journey({view,setView}) {
  const steps=[['Discover Partner','discover',Search],['Manage Exchanges','offers',ArrowLeftRight]]
  return (
    <div className="fixed bottom-0 left-0 right-0 z-30 border-t-2 border-black bg-white px-2 py-2">
      <div className="mx-auto flex max-w-7xl gap-2 overflow-x-auto">
        {steps.map(([label,id,Icon])=>(<React.Fragment key={label}>
          <button
            onClick={()=>setView(id)}
            className={`flex flex-1 items-center justify-center gap-2 px-4 py-3 text-center text-sm font-black border-2 border-black transition-all duration-150 ${view===id?'bg-blue-600 text-white shadow-[2px_2px_0_#000] hover:bg-blue-700 hover:-translate-y-[2px] hover:shadow-[4px_4px_0_#000] active:-translate-y-[1px] active:shadow-[1px_1px_0_#000]':'bg-white text-black shadow-[1px_1px_0_#000] hover:bg-blue-50 hover:-translate-y-[2px] hover:shadow-[4px_4px_0_#000] active:-translate-y-[1px] active:bg-blue-600 active:text-white active:shadow-[1px_1px_0_#000]'}`}
          >
            <span className={`flex h-8 w-8 items-center justify-center border-2 border-black rounded-md transition-colors duration-150 bg-white`}>
              <Icon size={15}/>
            </span>
            <span className={view===id?'text-white':'text-black'}>{label}</span>
          </button>
        </React.Fragment>))}
      </div>
    </div>
  )
}

function Profile({close, userId, userName, userEmail, userBio, teachSkills, learnSkills, setUserName, setUserBio, setTeachSkills, setLearnSkills, setEditMode, editMode}) {
  const [editName,setEditName]=useState(userName||'')
  const [editBio,setEditBio]=useState(userBio||'')
  const [newTeachSkill,setNewTeachSkill]=useState('')
  const [newTeachCategory,setNewTeachCategory]=useState('')
  const [newLearnSkill,setNewLearnSkill]=useState('')
  const [newLearnCategory,setNewLearnCategory]=useState('')
  const [loadingTeach,setLoadingTeach]=useState(false)
  const [teachError,setTeachError]=useState('')
  const [loadingLearn,setLoadingLearn]=useState(false)
  const [learnError,setLearnError]=useState('')

  useEffect(()=>{
    if(!userId) return
    fetchTeachSkills()
    fetchLearnSkills()
  },[userId])

  const fetchTeachSkills=async()=>{
    if(!userId) return
    try{
      const res=await fetch(`http://localhost:8080/api/users/${userId}/skills`)
      if(!res.ok) throw new Error('Failed to load skills')
      const data=await res.json()
      const teachOnly=data.filter(us=>us.type==='TEACH').map(us=>({
        userSkillId:us.id,
        skillName:us.skill.name,
        skillCategory:us.skill.category,
        type:us.type
      }))
      setTeachSkills(teachOnly)
    }catch(err){
      setTeachError(err.message)
    }
  }

  const fetchLearnSkills=async()=>{
    if(!userId) return
    try{
      const res=await fetch(`http://localhost:8080/api/users/${userId}/skills`)
      if(!res.ok) throw new Error('Failed to load skills')
      const data=await res.json()
      const learnOnly=data.filter(us=>us.type==='LEARN').map(us=>({
        userSkillId:us.id,
        skillName:us.skill.name,
        skillCategory:us.skill.category,
        type:us.type
      }))
      setLearnSkills(learnOnly)
    }catch(err){
      setLearnError(err.message)
    }
  }

  const addTeachSkill=async()=>{
    const name=newTeachSkill.trim()
    const category=newTeachCategory.trim()
    if(!name||!category) return
    if(teachSkills.some(s=>s.skillName.toLowerCase()===name.toLowerCase()&&s.skillCategory.toLowerCase()===category.toLowerCase())) return
    setLoadingTeach(true)
    setTeachError('')
    try{
      const res=await fetch(`http://localhost:8080/api/users/${userId}/skills`,{
        method:'POST',
        headers:{'Content-Type':'application/json'},
        body:JSON.stringify({name,category,type:'TEACH'})
      })
      if(!res.ok){
        const errData=await res.json().catch(()=>({}))
        throw new Error(errData.message||'Failed to add skill')
      }
      const userSkill=await res.json()
      setTeachSkills([...teachSkills,{
        userSkillId:userSkill.id,
        skillName:userSkill.skill.name,
        skillCategory:userSkill.skill.category,
        type:userSkill.type
      }])
      setNewTeachSkill('')
      setNewTeachCategory('')
    }catch(err){
      setTeachError(err.message)
    }finally{
      setLoadingTeach(false)
    }
  }

  const addLearnSkill=async()=>{
    const name=newLearnSkill.trim()
    const category=newLearnCategory.trim()
    if(!name||!category) return
    if(learnSkills.some(s=>s.skillName.toLowerCase()===name.toLowerCase()&&s.skillCategory.toLowerCase()===category.toLowerCase())) return
    setLoadingLearn(true)
    setLearnError('')
    try{
      const res=await fetch(`http://localhost:8080/api/users/${userId}/skills`,{
        method:'POST',
        headers:{'Content-Type':'application/json'},
        body:JSON.stringify({name,category,type:'LEARN'})
      })
      if(!res.ok){
        const errData=await res.json().catch(()=>({}))
        throw new Error(errData.message||'Failed to add skill')
      }
      const userSkill=await res.json()
      setLearnSkills([...learnSkills,{
        userSkillId:userSkill.id,
        skillName:userSkill.skill.name,
        skillCategory:userSkill.skill.category,
        type:userSkill.type
      }])
      setNewLearnSkill('')
      setNewLearnCategory('')
    }catch(err){
      setLearnError(err.message)
    }finally{
      setLoadingLearn(false)
    }
  }

  const removeTeachSkill=async(userSkillId)=>{
    if(!userId) return
    setLoadingTeach(true)
    setTeachError('')
    try{
      await fetch(`http://localhost:8080/api/users/${userId}/skills/${userSkillId}`,{method:'DELETE'})
      setTeachSkills(teachSkills.filter(s=>s.userSkillId!==userSkillId))
    }catch(err){
      setTeachError(err.message)
    }finally{
      setLoadingTeach(false)
    }
  }

  const removeLearnSkill=async(userSkillId)=>{
    if(!userId) return
    setLoadingLearn(true)
    setLearnError('')
    try{
      await fetch(`http://localhost:8080/api/users/${userId}/skills/${userSkillId}`,{method:'DELETE'})
      setLearnSkills(learnSkills.filter(s=>s.userSkillId!==userSkillId))
    }catch(err){
      setLearnError(err.message)
    }finally{
      setLoadingLearn(false)
    }
  }

  const saveProfile=()=>{
    setUserName(editName)
    setUserBio(editBio)
    setEditMode(false)
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4" onClick={close}>
      <div className="max-w-[820px] w-full max-h-[90vh] overflow-y-auto border-[3px] border-black bg-white p-8 shadow-[8px_8px_0_#2563eb] profile-panel" onClick={e=>e.stopPropagation()}>
        <div className="mb-6 flex items-start justify-between gap-4">
          <div>
            <p className="mono text-xs font-bold uppercase tracking-widest text-blue-700">User Profile</p>
            <h2 className="text-3xl font-black tracking-tight sm:text-4xl">My Profile<span className="text-blue-600">.</span></h2>
          </div>
          <button onClick={close} className="border-2 border-black p-2 hover:bg-blue-50" aria-label="Close profile"><X/></button>
        </div>

        <div className="mb-8 flex items-center gap-4">
          <div className="flex h-20 w-20 items-center justify-center border-2 border-black bg-blue-600 text-white font-black text-3xl uppercase">
            {userName ? userName.charAt(0).toUpperCase() : '?'}
          </div>
          <div>
            <h3 className="text-xl font-black">{userName || 'User'}</h3>
            <p className="mono text-[10px] font-bold uppercase text-gray-600">{userEmail || ''}</p>
          </div>
        </div>

          <div className="space-y-6">
            <div className="border-2 border-black p-6 shadow-[4px_4px_0_#000]">
              <div className="mb-6 flex items-center justify-between gap-4">
                <div>
                  <p className="mono text-xs font-bold uppercase tracking-widest text-blue-700">Personal Information</p>
                  <h4 className="text-xl font-black">Profile Details</h4>
                </div>
                {editMode ? (
                  <button onClick={()=>setEditMode(false)} className="border-2 border-black px-4 py-2 text-sm font-bold uppercase hover:bg-blue-50">Cancel</button>
                ) : (
                  <button onClick={()=>setEditMode(true)} className="border-2 border-black px-4 py-2 text-sm font-bold uppercase hover:bg-blue-50">Edit Profile</button>
                )}
              </div>

              {editMode ? (
                <div className="space-y-4">
                  <div className="space-y-2">
                    <label className="block text-sm font-bold">Full Name</label>
                    <input className="w-full border-2 border-black p-3 text-sm" value={editName} onChange={e=>setEditName(e.target.value)} required/>
                  </div>
                  <div className="space-y-2">
                    <label className="block text-sm font-bold">Bio</label>
                    <input className="w-full border-2 border-black p-3 text-sm" value={editBio} onChange={e=>setEditBio(e.target.value)} placeholder="Tell us about yourself"/>
                  </div>
                  <button onClick={saveProfile} className="border-2 border-black bg-blue-600 text-white px-6 py-3 font-black uppercase hover:bg-blue-700 active:scale-95">Save Changes</button>
                </div>
              ) : (
                <div className="space-y-5">
                  <div className="flex items-center justify-between gap-4">
                    <span className="mono text-xs font-bold uppercase text-gray-600">User ID</span>
                    <span className="font-black text-lg">{userId || '—'}</span>
                  </div>
                  <div className="flex items-center justify-between gap-4">
                    <span className="mono text-xs font-bold uppercase text-gray-600">Name</span>
                    <span className="font-black text-lg">{userName}</span>
                  </div>
                  <div className="flex items-center justify-between gap-4">
                    <span className="mono text-xs font-bold uppercase text-gray-600">Email</span>
                    <span className="font-black text-lg">{userEmail}</span>
                  </div>
                  <div className="flex items-center justify-between gap-4">
                    <span className="mono text-xs font-bold uppercase text-gray-600">Bio</span>
                    <span className="font-black text-lg">{userBio || 'No bio added yet'}</span>
                  </div>
                </div>
              )}
            </div>

            <div className="border-2 border-black p-6 shadow-[4px_4px_0_#000]">
              <div className="mb-6 flex items-start justify-between gap-4">
                <div>
                  <p className="mono text-xs font-bold uppercase tracking-widest text-blue-700">Skills I Can Teach</p>
                  <h4 className="text-xl font-black">Teaching Skills<span className="text-blue-600">.</span></h4>
                </div>
                {teachError && <p className="text-[10px] text-red-600 font-bold">{teachError}</p>}
              </div>

              <div className="space-y-3">
                {teachSkills.length === 0 ? (
                  <p className="text-[10px] text-gray-600 italic">No teaching skills added yet.</p>
                ) : (
                  teachSkills.map((s)=>(<div key={s.userSkillId} className="flex items-center justify-between gap-3 border-2 border-black px-4 py-3 bg-white"><span className="text-sm font-black">{s.skillName} <span className="mono text-[9px] text-gray-500">({s.skillCategory})</span></span><button onClick={()=>removeTeachSkill(s.userSkillId)} className="border-2 border-black px-3 py-2 text-[10px] font-bold hover:bg-red-600 hover:text-white active:scale-95">Remove</button></div>))
                )}
              </div>

              <div className="space-y-2 mt-6">
                <label className="block text-sm font-bold">Skill Name</label>
                <div className="grid gap-3 md:grid-cols-4">
                  <input type="text" value={newTeachSkill} onChange={e=>setNewTeachSkill(e.target.value)} placeholder="Skill name" className="field border-2 border-black p-3 text-sm"/>
                  <input type="text" value={newTeachCategory} onChange={e=>setNewTeachCategory(e.target.value)} placeholder="Category" className="field border-2 border-black p-3 text-sm"/>
                </div>
                <div className="mt-4">
                  <button onClick={addTeachSkill} className="border-2 border-black bg-blue-600 text-white px-6 py-3 font-black uppercase hover:bg-blue-700 active:scale-95" disabled={loadingTeach}>{loadingTeach?'Adding...':'Add Skill'}</button>
                </div>
              </div>
            </div>

            <div className="border-2 border-black p-6 shadow-[4px_4px_0_#000]">
              <div className="mb-6 flex items-start justify-between gap-4">
                <div>
                  <p className="mono text-xs font-bold uppercase tracking-widest text-pink-600">Skills I Want to Learn</p>
                  <h4 className="text-xl font-black">Learning Skills<span className="text-pink-600">.</span></h4>
                </div>
              </div>

              <div className="space-y-3">
                {learnError && <p className="text-[10px] text-red-600 font-bold">{learnError}</p>}
                {learnSkills.length === 0 ? (
                  <p className="text-[10px] text-gray-600 italic">No learning skills added yet.</p>
                ) : (
                  learnSkills.map((s)=>(<div key={s.userSkillId} className="flex items-center justify-between gap-3 border-2 border-black px-4 py-3 bg-white"><span className="text-sm font-black">{s.skillName} <span className="mono text-[9px] text-gray-500">({s.skillCategory})</span></span><button onClick={()=>removeLearnSkill(s.userSkillId)} className="border-2 border-black px-3 py-2 text-[10px] font-bold hover:bg-red-600 hover:text-white active:scale-95">Remove</button></div>))
                )}
              </div>

              <div className="space-y-2 mt-6">
                <label className="block text-sm font-bold">Skill Name</label>
                <div className="grid gap-3 md:grid-cols-4">
                  <input type="text" value={newLearnSkill} onChange={e=>setNewLearnSkill(e.target.value)} placeholder="Skill name" className="field border-2 border-black p-3 text-sm"/>
                  <input type="text" value={newLearnCategory} onChange={e=>setNewLearnCategory(e.target.value)} placeholder="Category" className="field border-2 border-black p-3 text-sm"/>
                </div>
                <div className="mt-4">
                  <button onClick={addLearnSkill} className="border-2 border-black bg-pink-600 text-white px-6 py-3 font-black uppercase hover:bg-pink-700 active:scale-95" disabled={loadingLearn}>{loadingLearn?'Adding...':'Add Skill'}</button>
                </div>
              </div>
            </div>
          </div>
      </div>
    </div>
  )
}

function App() {
  const [view,setView]=useState('login'),[mobileNav,setMobileNav]=useState(false),[toast,setToast]=useState('')
  const [userId,setUserId]=useState(null)
  const [userName,setUserName]=useState('')
  const [userEmail,setUserEmail]=useState('')
  const [userBio,setUserBio]=useState('')
  const [showAvatarMenu,setShowAvatarMenu]=useState(false)
  const [showProfile,setShowProfile]=useState(false)
  const [editMode,setEditMode]=useState(false)
  const [tempName,setTempName]=useState('')
  const [tempBio,setTempBio]=useState('')
  const [teachSkills,setTeachSkills]=useState([])
  const [learnSkills,setLearnSkills]=useState([])
  const [newSkillName,setNewSkillName]=useState('')
  const [newSkillType,setNewSkillType]=useState('TEACH')

  useEffect(()=>{
    try {
      const stored=localStorage.getItem('skillswap_user')
      if(stored) {
        const parsed=JSON.parse(stored)
        if(parsed && parsed.name && parsed.email) {
          setUserId(parsed.id || null)
          setUserName(parsed.name)
          setUserEmail(parsed.email)
          setUserBio(parsed.bio || '')
          setView('dashboard')
        }
      }
    } catch (e) {
      localStorage.removeItem('skillswap_user')
    }
  },[])

  const login=(name='',email='',id=null,bio='')=>{
    localStorage.setItem('skillswap_user',JSON.stringify({id,name,email,bio}))
    setUserId(id)
    setUserName(name)
    setUserEmail(email)
    setUserBio(bio)
    setView('dashboard')
    setToast('Welcome to SkillSwap!')
    setTimeout(()=>setToast(''),2500)
  }
  const signup=()=>{setToast('Sign-up flow is ready to connect to your backend.');setTimeout(()=>setToast(''),3000)}
  const logout=()=>{
    localStorage.removeItem('skillswap_user')
    setView('login')
    setUserId(null)
    setUserName('')
    setUserEmail('')
    setUserBio('')
    setTeachSkills([])
    setLearnSkills([])
    setShowAvatarMenu(false)
    setShowProfile(false)
    setEditMode(false)
    setToast('Logged out')
    setTimeout(()=>setToast(''),2000)
  }
  const userInitial=userName?userName.charAt(0).toUpperCase():'?'
  useEffect(()=>{
    const handleClickOutside=(e)=>{if(showAvatarMenu&&!e.target.closest('.avatar-dropdown'))setShowAvatarMenu(false);if(showProfile&&!e.target.closest('.profile-panel'))setShowProfile(false)}
    const handleKeyDown=(e)=>{if(e.key==='Escape'&&showProfile)setShowProfile(false)}
    document.addEventListener('mousedown',handleClickOutside)
    document.addEventListener('keydown',handleKeyDown)
    return ()=>{document.removeEventListener('mousedown',handleClickOutside);document.removeEventListener('keydown',handleKeyDown)}
  },[showAvatarMenu,showProfile])
  if(view==='login')return <Login onLogin={login} onSignup={signup}/>
  const titles={dashboard:'Dashboard',discover:'Discover',offers:'Offers',chat:'Messages',rooms:'Skill Rooms',settings:'Settings',profile:'Profile',faq:'FAQ'}
  return <div className="min-h-screen bg-blue-50 pb-20">
    <header className="sticky top-0 z-40 border-b-2 border-black bg-white"><div className="mx-auto flex max-w-7xl items-center justify-between gap-3 px-4 py-3"><button onClick={()=>setView('dashboard')}><Logo small/></button><nav className="hidden items-center gap-5 lg:flex"><button onClick={()=>document.getElementById('benefits')?.scrollIntoView({behavior:'smooth'})} className="text-sm font-bold hover:text-blue-700">Benefits</button><button onClick={()=>document.getElementById('how')?.scrollIntoView({behavior:'smooth'})} className="text-sm font-bold hover:text-blue-700">How it works</button><button onClick={()=>document.getElementById('about')?.scrollIntoView({behavior:'smooth'})} className="text-sm font-bold hover:text-blue-700">About Us</button></nav><div className="hidden items-center gap-2 sm:flex"><button onClick={()=>setView('discover')} className="px-3 py-2 text-xs font-extrabold uppercase">Join Waitlist</button>{userName?(<div className="relative"><button onClick={()=>setShowAvatarMenu(!showAvatarMenu)} className="avatar-dropdown flex h-8 w-8 items-center justify-center border-2 border-black rounded-full bg-blue-600 text-white font-black text-sm uppercase hover:bg-blue-700 transition-colors duration-200 active:scale-95">{userInitial}</button>{showAvatarMenu&&<div className="avatar-dropdown absolute right-0 mt-2 w-48 border-2 border-black bg-white shadow-[4px_4px_0_#000] z-50"><div className="px-3 py-2 border-b-2 border-black bg-blue-50"><p className="mono text-[10px] font-bold uppercase text-black">{userName}</p>{userEmail&&<p className="mono text-[10px] font-bold uppercase text-gray-600">{userEmail}</p>}</div><button onClick={()=>{setShowAvatarMenu(false);setShowProfile(true)}} className="w-full flex items-center gap-2 border-t-2 border-black px-3 py-2 text-xs font-bold uppercase hover:bg-blue-50 text-left"><UserRound size={14}/> View Profile</button><button onClick={logout} className="w-full flex items-center gap-2 border-t-2 border-black px-3 py-2 text-xs font-bold uppercase hover:bg-blue-50 text-left"><LogOut size={14}/> Logout</button></div>}</div>):(<><button onClick={signup} className="px-3 py-2 text-xs font-extrabold uppercase">Sign up</button><button onClick={()=>setView('login')} className="px-3 py-2 text-xs font-extrabold uppercase">Login</button></>)}</div><button className="border-2 border-black p-2 lg:hidden" onClick={()=>setMobileNav(!mobileNav)} aria-label="Toggle navigation">{mobileNav?<X/>:<Menu/>}</button></div>
      {mobileNav&&<div className="grid grid-cols-2 gap-2 border-t-2 border-black bg-white p-3 lg:hidden">{['Benefits','How it works','About Us','Sign up','Login','Join Waitlist'].map(x=><button key={x} onClick={()=>{setMobileNav(false);if(x==='Login')setView('login');else if(x==='Join Waitlist')setView('discover');else if(x==='Sign up')signup();else document.getElementById(x==='Benefits'?'benefits':x==='How it works'?'how':'about')?.scrollIntoView({behavior:'smooth'})}} className="border-2 border-black p-2 text-sm font-bold">{x}</button>)}</div>}
    </header>
    <div className="mx-auto grid max-w-7xl gap-6 px-4 py-6 lg:grid-cols-[230px_1fr]"><div className="hidden lg:block"><Sidebar view={view} setView={setView} logout={()=>setView('login')}/></div><main className="min-w-0">{view==='dashboard'?<Dashboard setView={setView}/>:view==='discover'?<Discover setView={setView} userId={userId}/>:view==='offers'?<Offers setView={setView}/>:view==='chat'?<Chat/>:view==='profile'?(<Profile close={()=>setShowProfile(false)} userId={userId} userName={userName} userEmail={userEmail} userBio={userBio} teachSkills={teachSkills} learnSkills={learnSkills} setUserName={setUserName} setUserBio={setUserBio} setTeachSkills={setTeachSkills} setLearnSkills={setLearnSkills} setEditMode={setEditMode} editMode={editMode}/>):<div className="border-2 border-black bg-white p-8 shadow-[4px_4px_0_#000]"><h1 className="text-3xl font-black">{titles[view]||'SkillSwap'}</h1><p className="mt-2 text-gray-600">This section is part of the interactive prototype. Explore Discover, Offers and Messages to try the main exchange journey.</p><Button primary onClick={()=>setView('discover')} className="mt-5">Explore partners</Button></div>}
      <div className="mt-10 grid gap-4 border-t-2 border-black pt-6 md:grid-cols-3" id="benefits"><div><Tag blue>WHY SKILLSWAP</Tag><h2 className="mt-2 text-xl font-black">Your skills have value.</h2><p className="mt-1 text-sm text-gray-600">Trade knowledge, grow confidence, and meet curious people.</p></div><div id="how"><Tag>HOW IT WORKS</Tag><h2 className="mt-2 text-xl font-black">Find. Exchange. Grow.</h2><p className="mt-1 text-sm text-gray-600">Discover a partner, agree on a skill swap, then learn together.</p></div><div id="about"><Tag>ABOUT US</Tag><h2 className="mt-2 text-xl font-black">Learning is better shared.</h2><p className="mt-1 text-sm text-gray-600">SkillSwap makes peer-to-peer learning accessible to everyone.</p></div></div>
    </main></div>
    <Journey view={view} setView={setView}/>
    {showProfile && (
      <Profile
        close={()=>setShowProfile(false)}
        userId={userId}
        userName={userName}
        userEmail={userEmail}
        userBio={userBio}
        teachSkills={teachSkills}
        learnSkills={learnSkills}
        setUserName={setUserName}
        setUserBio={setUserBio}
        setTeachSkills={setTeachSkills}
        setLearnSkills={setLearnSkills}
      />
    )}
    {toast&&<div role="status" className="fixed right-4 top-20 z-50 border-2 border-black bg-blue-600 px-4 py-3 text-sm font-bold text-white shadow-[4px_4px_0_#000]">{toast}</div>}
  </div>
}
export default App
