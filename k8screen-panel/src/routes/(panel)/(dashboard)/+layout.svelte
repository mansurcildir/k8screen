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
  import type { UserInfo } from '$lib/model/user/UserInfo';
  import { configAPI } from '$lib/service/config-service';
  import type { ConfigInfo } from '$lib/model/config/ConfigInfo';
  import type { UserConfig } from '$lib/model/user/UserConfig';
  import RefreshCw from 'lucide-svelte/icons/refresh-cw';
  import { refresh } from '$lib/store';
  import { toastService } from '$lib/service/toast-service';
  import { authAPI } from '$lib/service/auth-service';

  $: namespace = $page.params.namespace;

  $: breadcrumbs = extractBreadcrumbs($page.url.pathname);

  let showRefreshButton = false;

  $: {
    const urlPattern = /^\/namespaces\/[^/]+$/;
    showRefreshButton = urlPattern.test($page.url.pathname);
  }

  let loading = false;
  let namespaces: { title: string; url: string }[] = [];
  let user: UserInfo;
  let configs: ConfigInfo[] = [];

  const getNamespaces = () => {
    loading = true;
    namespaceAPI
      .getAllNamespaces()
      .then((res) => {
        namespaces = res.data.map((ns) => ({
          title: ns,
          url: `/namespaces/${ns}`
        }));
      })
      .catch((err) => {
        toastService.show(err.message, 'error');
      })
      .finally(() => (loading = false));
  };

  const getProfile = () => {
    userAPI
      .getProfile()
      .then((res) => {
        user = res.data;
      })
      .catch((err) => {
        toastService.show(err.message, 'error');
      });
  };

  const updateConfig = (config: string) => {
    loading = true;
    const userConfig: UserConfig = {
      config
    };

    userAPI.updateConfig(userConfig).then(() => {
      namespaceAPI
        .getAllNamespaces()
        .then((res) => {
          namespaces = res.data.map((ns) => ({
            title: ns,
            url: `/namespaces/${ns}`
          }));
        })
        .catch((err) => {
          toastService.show(err.message, 'error');
        })
        .finally(() => (loading = false));
    });
  };

  const uploadFile = (file: File) => {
    const formData = new FormData();
    formData.append('config', file);

    configAPI
      .uploadConfig(formData)
      .then(() => {
        const userConfig: UserConfig = {
          config: file.name
        };
        userAPI
          .updateConfig(userConfig)
          .then(() => {
            window.location.href = '/namespaces';
          })
          .catch((err) => {
            toastService.show(err.message, 'error');
          });
      })
      .catch((err) => {
        toastService.show(err.message, 'error');
      });
  };

  const deleteFile = async (fileName: string) => {
    configAPI.deleteConfig(fileName).then(() => {
      configAPI
        .getAllConfigs()
        .then((res) => {
          configs = res.data;
        })
        .catch((err) => {
          toastService.show(err.message, 'error');
        });
    });
  };

  onMount(() => {
    configAPI
      .getAllConfigs()
      .then((res) => {
        configs = res.data;
      })
      .catch((err) => {
        toastService.show(err.message, 'error');
      });
  });

  $: data = {
    user: {
      name: user?.username,
      email: user?.email,
      avatar: user?.avatarUrl || '/favicon.png',
      active_config: user?.active_config
    },
    updateConfig: updateConfig,
    uploadFile: uploadFile,
    deleteFile: deleteFile,
    configs: configs,
    navMain: [
      {
        title: 'Namespaces',
        url: '#',
        icon: BookOpen,
        isActive: true,
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
            isActive: true,
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
  <AppSidebar data={data} loading={loading} />
  <Sidebar.Inset>
    <header
      class="flex h-16 shrink-0 items-center gap-2 transition-[width,height] ease-linear group-has-[[data-collapsible=icon]]/sidebar-wrapper:h-12"
    >
      <div class="flex w-full items-center justify-between gap-2 px-4">
        <div class="flex items-center justify-between">
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
        {#if showRefreshButton}
          <Button class="ms-auto" variant="outline" size="icon" onclick={() => refresh(namespace)}>
            <RefreshCw />
            <span class="sr-only">Refresh</span>
          </Button>
        {/if}
        <Button variant="outline" size="icon" onclick={toggleMode}>
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
