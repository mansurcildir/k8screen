<script lang="ts">
  import NavMain from '$lib/components/nav-main.svelte';
  import NavUser from '$lib/components/nav-user.svelte';
  import TeamSwitcher from '$lib/components/config-switcher.svelte';
  import * as Sidebar from '$lib/components/ui/sidebar/index.js';

  export let data;
  export let loading: boolean;
</script>

<Sidebar.Root>
  <Sidebar.Header>
    <TeamSwitcher
      configs={data.configs}
      user={data.user}
      updateConfig={data.updateConfig}
      uploadFile={data.uploadFile}
      deleteFile={data.deleteFile}
    />
  </Sidebar.Header>
  <Sidebar.Content>
    <NavMain items={data.navMain} loading={loading} />
  </Sidebar.Content>
  <Sidebar.Footer>
    <NavUser user={data.user} />
  </Sidebar.Footer>
  <Sidebar.Rail />
</Sidebar.Root>
