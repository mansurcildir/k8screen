<script lang="ts">
  import { Dialog as DialogPrimitive } from 'bits-ui';
  import { fade } from 'svelte/transition';
  import { cn } from '$lib/utils.js';

  type ExtendedDialogOverlayProps = DialogPrimitive.OverlayProps & {
    transition?: any;
    transitionConfig?: { duration: number };
  };

  type $$Props = ExtendedDialogOverlayProps;

  let className: $$Props['class'] = undefined;
  export { className as class };
</script>

<DialogPrimitive.Overlay
  class={cn('bg-background/80 fixed inset-0 z-50 backdrop-blur-sm', className)}
  {...$$restProps}
/>
