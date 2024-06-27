<template>
  <faq-tpl :title="$t('faq.title')">
    <section>
      <header>
        <heading :level="3" xlarge>
          {{ $t('faq.event.link1') }}
        </heading>
      </header>
      <div>
        <div class="column is-12">
          <img src="/images/faq/event.png" alt="">
          <paragraph>
            {{ $t('faq.event.desc') }}
          </paragraph>
        </div>
      </div>
    </section>
  </faq-tpl>
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator'
import FaqTpl from '~/components/templates/faq/index.vue'
import Heading from '~/components/atoms/heading/index.vue'
import Paragraph from '~/components/atoms/paragraph/index.vue'

@Component({
  components: {
    FaqTpl,
    Heading,
    Paragraph
  }
})
export default class Faq extends Vue {
}
</script>

<style lang="scss" scoped>
  img {
    border: solid 1px black;
  }
</style>
