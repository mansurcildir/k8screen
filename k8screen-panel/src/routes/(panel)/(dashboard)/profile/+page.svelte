<script lang="ts">
  import ResetPassword from '$lib/components/profile/reset-password/reset-password.svelte';
  import ProfileInfo from '$lib/components/profile/profile-info/profile-info.svelte';
  import Accounts from '$lib/components/profile/accounts/accounts.svelte';
</script>

<div class="flex flex-col justify-center gap-2 lg:flex-row">
  <div class="w-full max-w-[600px]">
    <ProfileInfo />
  </div>

  <div class="w-full max-w-[600px]">
    <ResetPassword />
    <Accounts />
  </div>
</div>
