const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export { API_BASE_URL }

export async function createOffer(senderId, receiverId, offeredSkillId, requestedSkillId) {
  const res = await fetch(`${API_BASE_URL}/api/offers`, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({senderId, receiverId, offeredSkillId, requestedSkillId})
  })
  if (!res.ok) {
    const errData = await res.json().catch(() => ({}))
    throw new Error(errData.message || 'Failed to create offer')
  }
  return await res.json()
}

export async function getIncomingOffers(userId) {
  const res = await fetch(`${API_BASE_URL}/api/offers/incoming/${userId}`)
  if (!res.ok) throw new Error('Failed to load incoming offers')
  return await res.json()
}

export async function getOutgoingOffers(userId) {
  const res = await fetch(`${API_BASE_URL}/api/offers/outgoing/${userId}`)
  if (!res.ok) throw new Error('Failed to load outgoing offers')
  return await res.json()
}

export async function acceptOffer(offerId, userId) {
  const res = await fetch(`${API_BASE_URL}/api/offers/${offerId}/accept/${userId}`, {
    method: 'PUT'
  })
  if (!res.ok) {
    const errData = await res.json().catch(() => ({}))
    throw new Error(errData.message || 'Failed to accept offer')
  }
  return await res.json()
}

export async function rejectOffer(offerId, userId) {
  const res = await fetch(`${API_BASE_URL}/api/offers/${offerId}/reject/${userId}`, {
    method: 'PUT'
  })
  if (!res.ok) {
    const errData = await res.json().catch(() => ({}))
    throw new Error(errData.message || 'Failed to reject offer')
  }
  return await res.json()
}
