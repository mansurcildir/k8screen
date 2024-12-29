<script lang="ts">
  import { DropdownMenu as DropdownMenuPrimitive, type WithoutChild } from 'bits-ui';
  import Check from 'lucide-svelte/icons/check';
  import { cn } from '$lib/utils.js';

  let {
    ref = $bindable(null),
    checked = $bindable(false),
    class: className,
    children: childrenProp,
    ...restProps
  }: WithoutChild<DropdownMenuPrimitive.CheckboxItemProps> = $props();
</script>

<DropdownMenuPrimitive.CheckboxItem
  bind:ref={ref}
  bind:checked={checked}
  class={cn(
    'data-[highlighted]:bg-accent data-[highlighted]:text-accent-foreground relative flex cursor-default select-none items-center rounded-sm py-1.5 pl-8 pr-2 text-sm outline-none data-[disabled]:pointer-events-none data-[disabled]:opacity-50',
    className
  )}
  {...restProps}
>
  {#snippet children({ checked })}
    <span class="absolute left-2 flex size-3.5 items-center justify-center">
        <Check class={cn('size-4', !checked && 'text-transparent')} />
    </span>
  {/snippet}
</DropdownMenuPrimitive.CheckboxItem>
