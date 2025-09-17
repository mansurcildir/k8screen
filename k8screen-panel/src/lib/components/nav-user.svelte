<script lang="ts">
  import { goto } from '$app/navigation';
  import * as Avatar from '$lib/components/ui/avatar/index.js';
  import * as DropdownMenu from '$lib/components/ui/dropdown-menu/index.js';
  import * as Sidebar from '$lib/components/ui/sidebar/index.js';
  import { useSidebar } from '$lib/components/ui/sidebar/index.js';
  import { authAPI } from '$lib/service/auth-service';
  import { stripeAPI } from '$lib/service/stripe-service';
  import { toastService } from '$lib/service/toast-service';
  import BadgeCheck from 'lucide-svelte/icons/badge-check';
  import Bell from 'lucide-svelte/icons/bell';
  import ChevronsUpDown from 'lucide-svelte/icons/chevrons-up-down';
  import CreditCard from 'lucide-svelte/icons/credit-card';
  import LogOut from 'lucide-svelte/icons/log-out';
  import Sparkles from 'lucide-svelte/icons/sparkles';

  let { user }: { user: { name: string; email: string; avatar: string } } = $props();
  const sidebar = useSidebar();

  const logout = () => {
    authAPI.logout().then(() => {
      window.location.href = '/login';
    });
  };

  const redirectToStripePanel = async () => {
    stripeAPI
      .getPanelSession()
      .then((res) => {
        if (res.data) {
          window.location.href = res.data;
        }
      })
      .catch((err) => {
        toastService.show(err.message, 'error');
      });
  };
</script>

<Sidebar.Menu>
  <Sidebar.MenuItem>
    <DropdownMenu.Root>
      <DropdownMenu.Trigger>
        {#snippet child({ props })}
          <Sidebar.MenuButton
            size="lg"
            class="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
            {...props}
          >
            <Avatar.Root class="h-8 w-8 rounded-lg">
              <Avatar.Image src={user.avatar} alt={user.name} />
              <Avatar.Fallback class="rounded-lg">CN</Avatar.Fallback>
            </Avatar.Root>
            <div class="grid flex-1 text-left text-sm leading-tight">
              <span class="truncate font-semibold">{user.name}</span>
              <span class="truncate text-xs">{user.email}</span>
            </div>
            <ChevronsUpDown class="ml-auto size-4" />
          </Sidebar.MenuButton>
        {/snippet}
      </DropdownMenu.Trigger>
      <DropdownMenu.Content
        class="w-[--bits-dropdown-menu-anchor-width] min-w-56 rounded-lg"
        side={sidebar.isMobile ? 'bottom' : 'right'}
        align="end"
        sideOffset={4}
      >
        <DropdownMenu.Label
          class="cursor-pointer p-0 font-normal"
          onclick={() => {
            goto('/profile');
          }}
        >
          <div class="flex items-center gap-2 px-1 py-1.5 text-left text-sm">
            <Avatar.Root class="h-8 w-8 rounded-lg">
              <Avatar.Image src={user.avatar} alt={user.name} />
              <Avatar.Fallback class="rounded-lg">CN</Avatar.Fallback>
            </Avatar.Root>
            <div class="grid flex-1 text-left text-sm leading-tight">
              <span class="truncate font-semibold">{user.name}</span>
              <span class="truncate text-xs">{user.email}</span>
            </div>
          </div>
        </DropdownMenu.Label>
        <DropdownMenu.Separator />
        <DropdownMenu.Group>
          <DropdownMenu.Item class="cursor-pointer" onclick={redirectToStripePanel}>
            <Sparkles />
            Upgrade to Pro
          </DropdownMenu.Item>
        </DropdownMenu.Group>
        <DropdownMenu.Separator />
        <DropdownMenu.Group>
          <DropdownMenu.Item
            class="cursor-pointer"
            onclick={() => {
              goto('/profile');
            }}
          >
            <BadgeCheck />
            Account
          </DropdownMenu.Item>
          <DropdownMenu.Item
            class="cursor-pointer"
            onclick={() => {
              goto('/billing');
            }}
          >
            <CreditCard />
            Billing
          </DropdownMenu.Item>
          <DropdownMenu.Item>
            <Bell />
            Notifications
          </DropdownMenu.Item>
        </DropdownMenu.Group>
        <DropdownMenu.Separator />
        <DropdownMenu.Item
          class="cursor-pointer"
          onclick={() => {
            logout();
          }}
        >
          <LogOut />
          Log out
        </DropdownMenu.Item>
      </DropdownMenu.Content>
    </DropdownMenu.Root>
  </Sidebar.MenuItem>
</Sidebar.Menu>
