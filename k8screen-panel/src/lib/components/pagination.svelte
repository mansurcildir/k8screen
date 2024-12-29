<script lang="ts">
  let pageNum: number = 1;
  let totalPages: number = 1;
  export let data: any[] = [];
  export let paginated: any[] = [];
  export let pageSize: number;

  $: {
    totalPages = Math.ceil(data.length / pageSize);
    load(pageNum, pageSize);
  }

  const load = (page: number, size: number) => {
    const startIndex = (page - 1) * size;
    const endIndex = page * size;
    paginated = data.slice(startIndex, endIndex);
  };

  const goToPage = (page: number) => {
    if (page > 0 && page <= totalPages) {
      pageNum = page;
    }
  };
</script>

{#if totalPages > 1}
  <div class="flex flex-col items-center space-y-4">
    <div class="flex items-center space-x-2">
      <button
        class="px-3 py-1 rounded bg-gray-300 hover:bg-gray-200 disabled:bg-gray-200 text-white"
        on:click={() => goToPage(pageNum - 1)}
        disabled={pageNum === 1}
      >
        ‹
      </button>

      {#each Array(totalPages)
        .fill(0)
        .map((_, i) => i + 1) as num}
        <button
          class="px-3 py-1 rounded
               {num === pageNum ? 'bg-blue-500 text-white' : 'bg-gray-300 hover:bg-gray-200 text-white'}"
          on:click={() => goToPage(num)}
        >
          {num}
        </button>
      {/each}

      <button
        class="px-3 py-1 rounded bg-gray-300 hover:bg-gray-200 disabled:bg-gray-200 text-white"
        on:click={() => goToPage(pageNum + 1)}
        disabled={pageNum === totalPages}
      >
        ›
      </button>
    </div>
  </div>
{/if}
