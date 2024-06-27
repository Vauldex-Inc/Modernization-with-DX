<template>
  <agreements-template
    :agreed="agreed"
    @privacy-policy="privacyPolicyAgree"
    @terms-of-use="termsOfUseAgree"
    @click="agreeTermsPolicy" />
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator'
import AgreementsTemplate from '~/components/templates/signup/agreements/index.vue'

@Component({
  middleware: 'unauthenticated',
  layout: 'auth-level',
  components: {
    AgreementsTemplate
  }
})
export default class SignupAgreement extends Vue {
  agreedTerms: boolean = false
  agreedPolicy: boolean = false

  $cookies: any

  privacyPolicyAgree(checked: boolean) {
    this.agreedPolicy = checked
  }

  termsOfUseAgree(checked: boolean) {
    this.agreedTerms = checked
  }

  get agreed(): boolean {
    return this.agreedTerms && this.agreedPolicy
  }

  agreeTermsPolicy() {
    this.$cookies.set('terms', 'true')
    this.$router.push('/signup/account')
  }
}
</script>
