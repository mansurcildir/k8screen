<script>
  export let color = 'bg-primary';
  export let width = 'w-1';
  export let height = 'h-4';
</script>

<style>
  .delay-0 {
    animation-delay: 0s;
  }
  .delay-150 {
    animation-delay: 0.15s;
  }
  .delay-300 {
    animation-delay: 0.3s;
  }
</style>

<div {...$$props}>
  <div class="flex space-x-0.5">
    <div class={`${width} ${height} ${color} animate-bounce rounded-sm delay-0`}></div>
    <div class={`${width} ${height} ${color} animate-bounce rounded-sm delay-150`}></div>
    <div class={`${width} ${height} ${color} animate-bounce rounded-sm delay-300`}></div>
  </div>
</div>
