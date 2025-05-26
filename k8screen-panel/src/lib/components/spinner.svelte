<script lang="ts">
  let props = $props();
  let color = props.color ?? 'currentColor';
</script>

<div {...props}>
  <svg
    class="animate-spin text-white"
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 24 24"
    fill="none"
    stroke={color}
    stroke-width="2"
  >
    <circle cx="12" cy="12" r="10" class="opacity-25"></circle>
    <path d="M4 12a8 8 0 0 1 16 0" class="opacity-75"></path>
  </svg>
</div>
