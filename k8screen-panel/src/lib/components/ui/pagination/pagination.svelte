<script lang="ts">
  import { Pagination as PaginationPrimitive } from 'bits-ui';

  import { cn } from '$lib/utils.js';

  let {
    ref = $bindable(null),
    class: className,
    count = 0,
    perPage = 10,
    page = $bindable(1),
    siblingCount = 1,
    ...restProps
  }: PaginationPrimitive.RootProps = $props();
</script>

<PaginationPrimitive.Root
  bind:ref={ref}
  class={cn('mx-auto flex w-full flex-col items-center', className)}
  count={count}
  perPage={perPage}
  siblingCount={siblingCount}
  bind:page={page}
  {...restProps}
/>
