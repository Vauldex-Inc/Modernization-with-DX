<template>
  <material-tpl />
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
export default class MaterialsCreate extends Vue {}
</script>
