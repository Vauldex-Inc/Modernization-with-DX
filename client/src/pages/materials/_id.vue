<template>
  <div class="material">
    <div v-html="materialHtml" />
    <div id="footer">
      <span id="btn">
        <a href="/materials/how-to-download" target="_blank">
          <icon name="folder-download" />
          {{ $t("materials.how_to_download") }}
        </a>
        <a @click="printMaterial">
          <icon name="printer" />
          {{ $t("materials.print") }}
        </a>
      </span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
#app {
  font-family: "Avenir", Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

@media print {
  #footer {
    display: none;
  }
}

#footer{
  height: 40px;
  width: 100%;
  position: fixed;
  bottom: 0;
  text-align: center;
  margin-bottom: 5rem;
}

#btn {
  color: white;
  background: rgb(0, 0, 0); /* Fallback for older browsers without RGBA-support */
  background: rgba(0, 0, 0, 0.8);
  background-color: black;
  padding: 10px 50px;
  border-radius: 5px;
  a {
    font-size: 1.2em;
    color: white;
    padding-left: 10px;
  }
}
</style>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator'
import cheerio from 'cheerio'
import Icon from '~/components/atoms/icon/index.vue'

@Component({
  middleware: 'authenticated',
  layout: 'material-generation',
  components: {
    Icon
  }
})

export default class Material extends Vue {
  title: string = ''

  async asyncData({ $axios, store, params, query }: { $axios: any, store: any, params: any, query: any }) {
    const material = await store.dispatch('material/GET_MATERIAL_BY_ID', params.id)
    const insurance = (await store.dispatch('material/GET_INSURANCE'))?.data
    const healthLevel = (await store.dispatch('material/GET_PREFERENCE', 'insurance.health.max_level'))?.data
    const longTermRate = (await store.dispatch('material/GET_PREFERENCE', 'insurance.health.long_term_rate'))?.data
    const pensionLevel = (await store.dispatch('material/GET_PREFERENCE', 'insurance.pension.max_level'))?.data
    const residenceTaxRate = (await store.dispatch('material/GET_PREFERENCE', 'residence.tax_rate'))?.data
    const tenureAmount = (await store.dispatch('material/GET_PREFERENCE', 'tenure_old_age.pension_base_amount'))?.data
    const corporateTax = (await store.dispatch('material/GET_CORPORATE_TAX'))?.data
    const residenceTax = (await store.dispatch('material/GET_RESIDENCE_TAX'))?.data
    const remunerationTax = (await store.dispatch('material/GET_REMUNERATION_TAX'))?.data
    const monthlyTax = (await store.dispatch('material/GET_MONTHLY_TAX'))?.data
    const withholdingBonusTax = (await store.dispatch('material/GET_WITHHOLDING_BONUS_TAX'))?.data

    const ins = insurance.find((r: any) => r.prefecture === material.customer.social_insurance_prefecture)
    ins.health_premium /= 100
    ins.pension_premium /= 100
    const date = new Date(material.customer.birthday)
    const customCreatedAt = query.d && new Date(Number(query.d))
    const deferredInsurance = material.deferredInsurance
    deferredInsurance.corporate_tax_rate *= 100
    deferredInsurance.deductable_ratio *= 100
    deferredInsurance.return_rate_at65 *= 100
    const { data } = await $axios.get(process.env.materialUrl, {
      data: {
        material: {
          created_at: customCreatedAt || material.material.created_at,
          id: material.material.id
        },
        company: material.company,
        customer: {
          ...material.customer,
          birth: {
            year: date.getFullYear(),
            month: date.getMonth() + 1,
            day: date.getDate()
          },
          materialDisplaySettings: {
            displayItems: [],
            line1: '',
            line2: '',
            prefecture: '',
            postalCode: '',
            phoneNumber: '',
            email: '',
            userDisplayAddress: false,
            userDisplayNumber: false,
            userDisplayEmail: false
          }
        },
        tax: material.tax,
        life_insurance: material.insurance,
        deferred_insurance: deferredInsurance,
        material_sections: material.material.material_sections,
        parameters: {
          insurance: {
            ...ins,
            health_max_level: healthLevel,
            long_term_rate: longTermRate,
            pension_max_level: pensionLevel
          },
          corporate_taxes: corporateTax.map((r: any) => {
            r.percentage /= 100
            return r
          }),
          residence_taxes: {
            data: residenceTax.map((r: any) => {
              r.tax_rate /= 100
              return r
            }),
            tax_rate: parseFloat(residenceTaxRate)
          },
          remuneration_taxes: remunerationTax.map((r: any) => {
            r.tax_rate /= 100
            return r
          }),
          tenure_old_age_amount: tenureAmount,
          monthly_tax: monthlyTax,
          withholding_bonus_tax: withholdingBonusTax
        }
      }
    })

    const $ = cheerio.load(data)

    const styles = $('style')
      .toArray()
      .reduce((result: any, style: any) => result + $(style).html(), '')

    const style = `<style>${styles}</style>`

    const contents = $('body').html()
    return { materialHtml: `${style}${contents}`, name: material.customer.name }
  }

  printMaterial() {
    if (navigator.userAgent.includes('Firefox') ||
      navigator.userAgent.includes('Chrome')) {
      window.print()
    } else {
      document.execCommand('print', false, '')
    }
  }

  created() {
    this.setTitle()
  }

  setTitle() {
    const date = new Date()
    const timestamp = `${this.toDateString(date)}-${this.toTimeString(date)}`
    const name = (this as any).name
    this.title = `${name.last}${name.first}-${timestamp}`
  }

  toDateString(date: any) {
    const d = new Date(date).toISOString()
    return d.split('T')[0].split('-').join('')
  }

  toTimeString(date: any) {
    const options = { hour12: false, hour: '2-digit', minute: '2-digit' }
    return new Date(date).toLocaleTimeString([], options).replace(/(:|\s)/g, '')
  }

  head() {
    return { title: this.title }
  }
}
</script>
