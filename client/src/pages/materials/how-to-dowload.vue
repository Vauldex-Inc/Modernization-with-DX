<template>
  <how-to-download-tpl />
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator'
import HowToDownloadTpl from '~/components/templates/materials/how-to-download.vue'

@Component({
  middleware: 'authenticated',
  components: {
    HowToDownloadTpl
  }
})
export default class HowToDownload extends Vue {}
</script>
