<script lang="ts">
  import ChevronLeftIcon from 'lucide-svelte/icons/chevron-left';
  import ChevronRightIcon from 'lucide-svelte/icons/chevron-right';
  import * as Pagination from '$lib/components/ui/pagination/index.js';

  export let count;
  export let perPage;
  export let load: (pageNum: number) => void;
</script>

<Pagination.Root class="mb-5" count={count} perPage={perPage}>
  {#snippet children({ pages, currentPage })}
    <Pagination.Content>
      <Pagination.Item>
        <Pagination.PrevButton onclick={() => load(currentPage - 1)}>
          <ChevronLeftIcon class="size-4" />
          <span class="hidden sm:block">Previous</span>
        </Pagination.PrevButton>
      </Pagination.Item>
      {#each pages as page (page.key)}
        {#if page.type === 'ellipsis'}
          <Pagination.Item>
            <Pagination.Ellipsis />
          </Pagination.Item>
        {:else}
          <Pagination.Item>
            <Pagination.Link
              onclick={() => {
                load(page.value);
              }}
              page={page}
              isActive={currentPage === page.value}
            >
              {page.value}
            </Pagination.Link>
          </Pagination.Item>
        {/if}
      {/each}
      <Pagination.Item>
        <Pagination.NextButton onclick={() => load(currentPage + 1)}>
          <span class="hidden sm:block">Next</span>
          <ChevronRightIcon class="size-4" />
        </Pagination.NextButton>
      </Pagination.Item>
    </Pagination.Content>
  {/snippet}
</Pagination.Root>
