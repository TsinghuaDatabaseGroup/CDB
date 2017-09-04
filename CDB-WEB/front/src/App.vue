<template>
  <div id="app">
    <div v-if="logined">
      <crowd-show></crowd-show>
    </div>
    <div v-else style="margin-top: 60px">
      <img src="./assets/login.jpg">
      <hello></hello>
    </div>
  </div>
</template>

<script>
import Hello from './components/Hello'
import CrowdShow from './components/CrowdShow'
import store from './store/index'
import * as types from './store/mutation_types'

export default {
  store,
  name: 'app',
  data() {
    return {
    };
  },
  computed: {
    logined() {
      return !!store.state.token;
    }
  },
  methods: {
  },
  components: {
    Hello,
    CrowdShow
  }
}
</script>

<style>
  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    margin-top: 0px;
  }
  .clickable:hover {
    cursor: pointer;
  }

  .clearfix:after {
    content: ".";
    display: block;
    height: 0;
    visibility: hidden;
    clear: both;
  }
</style>
