<template>
  <materials-tpl :materials="materials" />
</template>

<script lang="ts">
import { Component, Vue, namespace } from 'nuxt-property-decorator'
import IMaterial from '~/types/material'
import MaterialsTpl from '~/components/templates/materials/index.vue'

const Module = namespace('material')

@Component({
  middleware: 'authenticated',
  components: {
    MaterialsTpl
  }
})
export default class Materials extends Vue {
  @Module.State('materials') materials !: Array<IMaterial>

  fetch({ store }: { store: any }) {
    return store.dispatch('material/GET')
  }
}
</script>
