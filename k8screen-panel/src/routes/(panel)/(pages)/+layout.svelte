<script lang="ts">
  import '../../../app.css';
  import BookOpen from 'lucide-svelte/icons/book-open';
  import AppSidebar from '$lib/components/app-sidebar.svelte';
  import * as Breadcrumb from '$lib/components/ui/breadcrumb/index.js';
  import { Separator } from '$lib/components/ui/separator/index.js';
  import * as Sidebar from '$lib/components/ui/sidebar/index.js';
  import { namespaceAPI } from '$lib/service/namespace-service';
  import { onMount } from 'svelte';
  import { extractBreadcrumbs } from '$lib/utils';
  import { page } from '$app/stores';
  import { ModeWatcher } from 'mode-watcher';
  import Sun from 'lucide-svelte/icons/sun';
  import Moon from 'lucide-svelte/icons/moon';
  import { toggleMode } from 'mode-watcher';
  import { Button } from '$lib/components/ui/button/index.js';
  import { userAPI } from '$lib/service/user-service';
  import type { UserItem } from '$lib/model/user/UserItem';
  import { configAPI } from '$lib/service/config-service';
  import type { ConfigItem } from '$lib/model/config/ConfigItem';
  import type { UserConfig } from '$lib/model/user/UserConfig';

  let breadcrumbs = extractBreadcrumbs($page.url.pathname);

  $: {
    if (breadcrumbs) {
      breadcrumbs = extractBreadcrumbs($page.url.pathname);
    }
  }

  let namespaces: { title: string; url: string }[] = [];
  let user: UserItem;
  let configs: ConfigItem[] = [];

  const getNamespaces = () => {
    namespaceAPI.getAllNamespaces().then((data) => {
      namespaces = data.map((ns) => ({
        title: ns,
        url: `/namespaces/${ns}`
      }));
    });
  };

  const getProfile = () => {
    userAPI.getProfile().then((data: UserItem) => {
      user = data;
    });
  };

  const updateConfig = (config: string) => {
    const userConfig: UserConfig = {
      config
    };
    userAPI.updateConfig(userConfig).then(() => {
      getNamespaces();
      getProfile();
    });
  };

  onMount(() => {
    configAPI.getAllConfigs().then((d) => {
      configs = d;
    });
  });

  $: data = {
    user: {
      name: user?.username,
      email: user?.email,
      avatar: user?.picture || '/k8s-logo.png',
      config: user?.config
    },
    updateConfig: updateConfig,
    configs: configs,
    navMain: [
      {
        title: 'Namespaces',
        url: '#',
        icon: BookOpen,
        items: namespaces
      }
    ]
  };

  // Fetch namespaces on mount and update `navMain`
  onMount(async () => {
    try {
      getNamespaces();
      getProfile();

      // Update navMain with the fetched namespaces
      data = {
        ...data,
        navMain: [
          {
            title: 'Namespaces',
            url: '#',
            icon: BookOpen,
            items: namespaces
          }
        ]
      };
    } catch (error) {
      console.error('Error fetching namespaces:', error);
    }
  });
</script>

<Sidebar.Provider>
  <AppSidebar data={data} />
  <Sidebar.Inset>
    <header
      class="flex h-16 shrink-0 items-center gap-2 transition-[width,height] ease-linear group-has-[[data-collapsible=icon]]/sidebar-wrapper:h-12"
    >
      <div class="flex justify-between items-center gap-2 px-4 w-full">
        <div class="flex justify-between items-center">
          <Sidebar.Trigger class="-ml-1" />
          <Separator orientation="vertical" class="mr-2 h-4" />
          <Breadcrumb.Root>
            <Breadcrumb.List>
              {#each breadcrumbs as breadcrumb, index}
                <Breadcrumb.Item class="hidden md:block">
                  <Breadcrumb.Link href={breadcrumb.link}>{breadcrumb.text}</Breadcrumb.Link>
                </Breadcrumb.Item>
                {#if index !== breadcrumbs.length - 1}
                  <Breadcrumb.Separator class="hidden md:block" />
                {/if}
              {/each}
            </Breadcrumb.List>
          </Breadcrumb.Root>
        </div>
        <Button class="ms-auto" variant="outline" size="icon" onclick={toggleMode}>
          <Sun class="h-[1.2rem] w-[1.2rem] rotate-0 scale-100 transition-all dark:-rotate-90 dark:scale-0" />
          <Moon class="absolute h-[1.2rem] w-[1.2rem] rotate-90 scale-0 transition-all dark:rotate-0 dark:scale-100" />
          <span class="sr-only">Toggle theme</span>
        </Button>
      </div>
    </header>
    <div class="flex flex-1 flex-col gap-4 p-4 pt-0">
      <slot />
    </div>
  </Sidebar.Inset>
</Sidebar.Provider>

<ModeWatcher />
