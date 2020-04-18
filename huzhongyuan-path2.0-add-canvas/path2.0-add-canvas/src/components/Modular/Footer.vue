<template>
    <div class="footer">
        <span>Copyright © 重庆邮电大学信管工作室 2018</span>
    </div>
</template>
<script>
  export default {
    data() {
      return {
        activeIndex: '1',
        activeIndex2: '1'
      };
    },
    methods: {
      handleSelect(key, keyPath) {
        console.log(key, keyPath);
      }
    }
  }
</script>

<style>
.footer {
    height: 50px;
    background-color: #1a252f;
    opacity: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    color:white;
    position: fixed;
    bottom: 0px;
    left: 0px;
    width: 100%;
    z-index: 1002;
}
</style>
