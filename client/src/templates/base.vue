<template>
  <div>
    <navbar />
    <main>
      <header>
        <heading :level="1" xxlarge>
          {{ title }}
        </heading>
      </header>
      <slot />
    </main>
    <section v-if="$slots.results" class="columns">
      <slot name="results" />
    </section>
    <cs-footer v-if="$slots.left || $slots.right">
      <template v-slot:left>
        <slot name="left" />
      </template>
      <template v-slot:right>
        <slot name="right" />
      </template>
    </cs-footer>
  </div>
</template>

<style lang="scss" scoped>
  main {
    max-width: 800px;
    width: 100%;
    margin: 0 auto;
    @include touch() {
      width: 100%;
      padding: $space-xxs;
    }
    header {
      text-align: center;
      margin: {
        top: $space-md;
        bottom: $space-md;
      }
    }
    .columns {
      display: flex;
      justify-content: center;
    }
  }
</style>

<script lang="ts">
import { Component, Prop, Vue } from 'nuxt-property-decorator'
import Heading from '~/components/atoms/heading/index.vue'
import Navbar from '~/components/organisms/navbar/index.vue'
import CsFooter from '~/components/atoms/footer/index.vue'

@Component({
  components: {
    Heading,
    Navbar,
    CsFooter
  }
})
export default class DefaultTemplate extends Vue {
  @Prop()
  title !: string
}
</script>
