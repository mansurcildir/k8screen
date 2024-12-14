<script lang="ts">
  import { page } from "$app/stores";
	import * as Collapsible from "$lib/components/ui/collapsible/index.js";
	import * as Sidebar from "$lib/components/ui/sidebar/index.js";
	import ChevronRight from "lucide-svelte/icons/chevron-right";
	import Plus from "lucide-svelte/icons/plus";
  import Button from "./ui/button/button.svelte";
	import Check from "lucide-svelte/icons/check";
	import X from "lucide-svelte/icons/x";
  import { namespaceAPI } from "$lib/service/namespace-service";

	let namespace: string = $state("");
	let visible: boolean = $state(false);

	let {
		items,
	}: {
		items: {
			title: string;
			url: string;
			// this should be `Component` after lucide-svelte updates types
			// eslint-disable-next-line @typescript-eslint/no-explicit-any
			icon?: any;
			isActive?: boolean;
			items?: {
				title: string;
				url: string;
			}[];
		}[];
	} = $props();

	const createNamespace = () => {
		namespaceAPI.createNamespace(namespace)
		.finally(() => visible = false);
	}

	const removeNamespace = () => {
		namespace = "";
		visible = false;
	}

</script>

<Sidebar.Group>
	<Sidebar.GroupLabel>Platform</Sidebar.GroupLabel>
	<Sidebar.Menu>
		{#each items as mainItem (mainItem.title)}
			<Collapsible.Root open={mainItem.isActive} class="group/collapsible">
				{#snippet child({ props })}
					<Sidebar.MenuItem {...props}>
						<Collapsible.Trigger>
							{#snippet child({ props })}
								<Sidebar.MenuButton {...props}>
									{#snippet tooltipContent()}
										{mainItem.title}
									{/snippet}
									{#if mainItem.icon}
										<mainItem.icon />
									{/if}
									<span>{mainItem.title}</span>
									<ChevronRight
										class="ml-auto transition-transform duration-200 group-data-[state=open]/collapsible:rotate-90"
									/>
								</Sidebar.MenuButton>
							{/snippet}
						</Collapsible.Trigger>
						<Collapsible.Content>
							{#if mainItem.items}
								<Sidebar.MenuSub>
									<Sidebar.MenuSubItem>
										<Sidebar.MenuSubButton isActive={$page.params.namespace == "Add namespace"}>
											{#snippet child({ props })}

											<button onclick={() => visible = true} {...props}>
												<div class="flex justify-between items-center w-full">
													{"Add namespace"} 
													<Plus class="size-5 hidden group-hover:inline-flex"/>
												</div>
											</button>
											{/snippet}
										</Sidebar.MenuSubButton>
									</Sidebar.MenuSubItem>

									{#if (visible)}
									<Sidebar.MenuSubItem>
										<Sidebar.MenuSubButton>
											{#snippet child({ props })}
												<div class="flex justify-between items-center gap-2">
											      <input type="text" bind:value={namespace} placeholder="namespace..." {...props}/>
												  <form onsubmit={createNamespace}>
													<div class="flex justify-end gap-1">
														{#if (namespace)}
														<Button type="submit" class="ms-auto h-8 w-5" variant="outline" size="sm">
														  <Check/>
														</Button>
														{/if}
														<Button onclick={removeNamespace} type="button" class="ms-auto h-8 w-5" variant="outline" size="sm">
														  <X/>
														</Button>
														</div>
												  </form>
												</div>
											{/snippet}
										</Sidebar.MenuSubButton>
									</Sidebar.MenuSubItem>
									{/if}
								

									{#each mainItem.items as subItem (subItem.title)}
										<Sidebar.MenuSubItem>
											<Sidebar.MenuSubButton isActive={$page.params.namespace == subItem.title}>
												{#snippet child({ props })}
													<a href={subItem.url} {...props}>
														<span>{subItem.title}</span>
													</a>
												{/snippet}
											</Sidebar.MenuSubButton>
										</Sidebar.MenuSubItem>
									{/each}
								</Sidebar.MenuSub>
							{/if}
						</Collapsible.Content>
					</Sidebar.MenuItem>
				{/snippet}
			</Collapsible.Root>
		{/each}
	</Sidebar.Menu>
</Sidebar.Group>
