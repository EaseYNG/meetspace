import { ref } from 'vue'
import { signupActivityAPI, quitActivityAPI } from '@/api/activity'
import { getParticipatedActivitiesAPI, getCreatedActivitiesAPI, getSignedUpActivitiesAPI } from '@/api/user'
import { ElMessage } from 'element-plus'
import type { ActivityVO } from '@/types'
import { ActivityStatus } from '@/types'

const participatedIds = ref<Set<number>>(new Set())
const loaded = ref(false)
let loadingPromise: Promise<void> | null = null

async function ensureLoaded() {
  if (loaded.value) return
  if (loadingPromise) return loadingPromise
  loadingPromise = (async () => {
    try {
      const [p, c, s] = await Promise.all([
        getParticipatedActivitiesAPI(),
        getCreatedActivitiesAPI(),
        getSignedUpActivitiesAPI(),
      ])
      const ids = new Set<number>()
      for (const res of [p, c, s]) {
        for (const a of res.data.data ?? []) {
          ids.add(a.id)
        }
      }
      participatedIds.value = ids
    } catch {
      // 加载失败时静默处理，后续 canSignup/canQuit 会回退到 activity.isParticipant
    } finally {
      loaded.value = true
      loadingPromise = null
    }
  })()
  return loadingPromise
}

export function useActivityActions() {
  function isParticipant(activity: ActivityVO): boolean {
    if (activity.isParticipant !== undefined && activity.isParticipant !== null) {
      return activity.isParticipant
    }
    return participatedIds.value.has(activity.id)
  }

  function canSignup(activity: ActivityVO): boolean {
    return activity.status === ActivityStatus.READY && !isParticipant(activity)
  }

  function canQuit(activity: ActivityVO): boolean {
    return (
      (activity.status === ActivityStatus.READY || activity.status === ActivityStatus.CLOSED) &&
      isParticipant(activity)
    )
  }

  async function signup(activityId: number) {
    await signupActivityAPI(activityId)
    const next = new Set(participatedIds.value)
    next.add(activityId)
    participatedIds.value = next
    ElMessage.success('报名成功')
  }

  async function quit(activityId: number) {
    await quitActivityAPI(activityId)
    const next = new Set(participatedIds.value)
    next.delete(activityId)
    participatedIds.value = next
    ElMessage.success('已退出活动')
  }

  function markParticipant(activityId: number) {
    const next = new Set(participatedIds.value)
    next.add(activityId)
    participatedIds.value = next
  }

  return {
    participatedIds,
    loaded,
    ensureLoaded,
    canSignup,
    canQuit,
    signup,
    quit,
    markParticipant,
  }
}
