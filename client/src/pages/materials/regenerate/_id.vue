<template>
  <material-tpl :id="$router.currentRoute.params.id" />
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator'
import MaterialTpl from '~/components/templates/materials/create/index.vue'

@Component({
  middleware: 'authenticated',
  components: {
    MaterialTpl
  }
})
export default class MaterialsRegenerate extends Vue {}
</script>
