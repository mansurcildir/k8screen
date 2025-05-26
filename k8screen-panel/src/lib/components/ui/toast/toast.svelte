<script lang="ts">
  import AlertXIcon from 'lucide-svelte/icons/x';
  import * as Alert from '$lib/components/ui/alert/index.js';
  import { toasts, toastService } from '$lib/service/toast-service';
  import './toast.css';

  const removeToast = (index: number) => {
    toastService.remove(index);
  };
</script>

<div class="container z-50 flex w-full max-w-[500px] flex-col gap-2">
  {#each $toasts as toast}
    <Alert.Root class="custom-toast">
      <AlertXIcon onclick={() => removeToast(toast.id)} class="size-4 cursor-pointer" />
      <Alert.Title>{toast.type.toUpperCase()}</Alert.Title>
      <Alert.Description>{toast.message}</Alert.Description>
    </Alert.Root>
  {/each}
</div>
