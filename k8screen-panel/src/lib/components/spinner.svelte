<script>
  export let size = '10'; // Spinner boyutu için varsayılan sınıf
</script>

<div class={`flex items-center justify-center h-${size} w-${size}`}>
  <svg
    class="animate-spin text-white"
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="2"
  >
    <circle cx="12" cy="12" r="10" class="opacity-25" />
    <path d="M4 12a8 8 0 0 1 16 0" class="opacity-75" />
  </svg>
</div>

<style>
  /* Dilerseniz ek stil ekleyebilirsiniz */
</style>
