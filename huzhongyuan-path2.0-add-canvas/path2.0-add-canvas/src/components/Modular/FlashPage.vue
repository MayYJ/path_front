<template>
    <div>
        
    </div>
</template>
<script>
  export default {
    data() {
      return {

      };
    },
    methods: {

    },
    mounted () {
      this.$router.go(0)
      let that = this;
      this.$router.push({
        path: "/DataPerpare",
        query: { id: that.$route.query.id }
      });
    }
  }
</script>

<style>

</style>
