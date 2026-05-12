<template>
  <div class="map-picker-overlay" @click.self="$emit('close')">
    <div class="map-picker glass-card">
      <div class="picker-header">
        <h3>{{ $t('selectLocation') }}</h3>
        <el-button text @click="$emit('close')">
          <el-icon :size="18"><Close /></el-icon>
        </el-button>
      </div>
      <div ref="mapContainer" class="map-container"></div>
      <div class="map-center-marker">
        <el-icon :size="32" color="#4caf50"><LocationFilled /></el-icon>
      </div>
      <div class="picker-footer">
        <div class="address-info">
          <span v-if="addressLoading">{{ $t('addressLoading') }}</span>
          <span v-else-if="selectedAddress">{{ selectedAddress }}</span>
          <span v-else class="hint">{{ $t('selectLocation') }}</span>
        </div>
        <div class="coord-info" v-if="center">
          {{ center.lat.toFixed(6) }}, {{ center.lng.toFixed(6) }}
        </div>
        <div class="picker-actions">
          <el-button @click="locateMe">
            <el-icon><Aim /></el-icon>
            {{ $t('getCurrentLocation') }}
          </el-button>
          <el-button type="primary" :disabled="!selectedAddress" @click="confirm">
            {{ $t('confirm') }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import AMapLoader from '@amap/amap-jsapi-loader'

const emit = defineEmits(['confirm', 'close'])
const mapContainer = ref(null)
const center = ref({ lat: 39.9042, lng: 116.4074 })
const selectedAddress = ref('')
const addressLoading = ref(false)

let map = null
let marker = null
let geocoder = null

async function initMap() {
  try {
    const AMap = await AMapLoader.load({
      key: import.meta.env.VITE_AMAP_KEY,
      version: '2.0',
    })

    map = new AMap.Map(mapContainer.value, {
      zoom: 14,
      center: [center.value.lng, center.value.lat],
      layers: [new AMap.TileLayer()],
    })

    marker = new AMap.Marker({
      position: [center.value.lng, center.value.lat],
      draggable: true,
    })
    map.add(marker)

    AMap.plugin('AMap.Geolocation', () => {
      geocoder = new AMap.Geolocation()
    })
    AMap.plugin('AMap.Geocoder', () => {
      geocoder = new AMap.Geocoder({ city: '全国', radius: 1000 })
    })

    map.on('click', (e) => {
      const lnglat = e.lnglat
      center.value = { lat: lnglat.getLat(), lng: lnglat.getLng() }
      marker.setPosition([lnglat.getLng(), lnglat.getLat()])
      reverseGeocode(lnglat.getLat(), lnglat.getLng())
    })

    marker.on('dragend', () => {
      const pos = marker.getPosition()
      center.value = { lat: pos.getLat(), lng: pos.getLng() }
      reverseGeocode(pos.getLat(), pos.getLng())
    })

    reverseGeocode(center.value.lat, center.value.lng)
  } catch (e) {
    console.warn('Map init failed:', e)
  }
}

async function reverseGeocode(lat, lng) {
  addressLoading.value = true
  selectedAddress.value = ''
  try {
    const AMap = await AMapLoader.load({ key: import.meta.env.VITE_AMAP_KEY, version: '2.0' })
    const geocoder = new AMap.Geocoder({ city: '全国', radius: 1000 })
    geocoder.getAddress([lng, lat], (status, result) => {
      if (status === 'complete' && result.info === 'OK') {
        selectedAddress.value = result.regeocode.formattedAddress || ''
      }
      addressLoading.value = false
    })
  } catch {
    addressLoading.value = false
  }
}

async function locateMe() {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        const lat = pos.coords.latitude
        const lng = pos.coords.longitude
        center.value = { lat, lng }
        if (map) {
          map.setCenter([lng, lat])
          marker.setPosition([lng, lat])
        }
        reverseGeocode(lat, lng)
      },
      () => {},
      { enableHighAccuracy: true, timeout: 10000 },
    )
  }
}

function confirm() {
  emit('confirm', {
    lat: center.value.lat,
    lng: center.value.lng,
    address: selectedAddress.value,
  })
}

onMounted(() => { initMap() })
onBeforeUnmount(() => { map?.destroy() })
</script>

<style scoped>
.map-picker-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
}

.map-picker {
  width: 100%;
  max-width: 520px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 0;
}

.picker-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid var(--surface-border);
}

.picker-header h3 {
  font-size: 17px;
  font-weight: 600;
  color: var(--text);
}

.map-container {
  height: 360px;
  width: 100%;
  position: relative;
}

.map-center-marker {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -100%);
  pointer-events: none;
  z-index: 10;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.3));
}

.picker-footer {
  padding: 16px 20px;
  border-top: 1px solid var(--surface-border);
}

.address-info {
  font-size: 14px;
  color: var(--text);
  margin-bottom: 4px;
  min-height: 20px;
}

.address-info .hint {
  color: var(--text-secondary);
}

.coord-info {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 12px;
}

.picker-actions {
  display: flex;
  gap: 12px;
}

.picker-actions .el-button {
  flex: 1;
}
</style>
