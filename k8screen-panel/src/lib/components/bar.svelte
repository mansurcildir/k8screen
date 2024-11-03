<script>
  export let color = 'bg-black';
  export let width = 'w-1'; 
  export let height = 'h-4'; 
</script>

<div class="flex space-x-0.5">
  <div class={`${width} ${height} ${color} rounded-sm animate-bounce delay-0`}></div>
  <div class={`${width} ${height} ${color} rounded-sm animate-bounce delay-150`}></div>
  <div class={`${width} ${height} ${color} rounded-sm animate-bounce delay-300`}></div>
</div>

<style>
  /* Dalga etkisi için her çubuğa farklı bir gecikme ekleyelim */
  .delay-0 { animation-delay: 0s; }
  .delay-150 { animation-delay: 0.15s; }
  .delay-300 { animation-delay: 0.3s; }
</style>
