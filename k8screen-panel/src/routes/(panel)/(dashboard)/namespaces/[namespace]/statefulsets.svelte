<script lang="ts">
  import Bar from '$lib/components/bar.svelte';
  import IconEllipsis from 'lucide-svelte/icons/ellipsis';
  import Pagination from '$lib/components/pagination.svelte';
  import Terminal from '$lib/components/terminal.svelte';
  import Badge from '$lib/components/ui/badge/badge.svelte';
  import Button from '$lib/components/ui/button/button.svelte';
  import * as DropdownMenu from '$lib/components/ui/dropdown-menu';
  import * as Table from '$lib/components/ui/table';
  import type { StatefulSet } from '$lib/model/StatefulSet';
  import { statefulSetAPI } from '$lib/service/statefulset-service';
  import { getAllStatefulSets, loadingStatefulSet, statefulSets } from '$lib/store';
  import * as yaml from 'yaml';

  export let namespace;

  let perPage: number = 5;
  let details: string;
  let paginated: StatefulSet[] = [];
  let k8sItem: string;
  let open: boolean;

  $: if (namespace) {
    getAllStatefulSets(namespace);
  }

  const load = (page: number) => {
    const startIndex = (page - 1) * perPage;
    const endIndex = page * perPage;
    paginated = $statefulSets.slice(startIndex, endIndex);
  };

  const getDetails = async (): Promise<string> => {
    open = true;
    details = await statefulSetAPI.getStatefulSetDetails(namespace, k8sItem);
    return details;
  };

  const updateItem = async () => {
    details = await statefulSetAPI.updateStatefulSet(namespace, k8sItem, yaml.parse(k8sItem));
    return details;
  };
</script>

<div class="flex flex-col" style="height: calc(100vh - 150px);">
  <div class="flex flex-grow flex-col justify-between gap-8 overflow-auto">
    <Table.Root>
      <Table.Header>
        <Table.Row>
          <Table.Head>NAME</Table.Head>
          <Table.Head>READY</Table.Head>
          <Table.Head>AGE</Table.Head>
          <Table.Head>OPTIONS</Table.Head>
        </Table.Row>
      </Table.Header>
      <Table.Body>
        {#if $loadingStatefulSet}
          <Table.Row>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
          </Table.Row>
        {:else}
          {#each paginated as statefulSet}
            <Table.Row
              on:click={() => {
                k8sItem = statefulSet.name;
                open = true;
              }}
              class="cursor-pointer"
            >
              <Table.Cell>{statefulSet.name}</Table.Cell>
              <Table.Cell>
                <Badge
                  class="flex w-20 justify-center"
                  variant={statefulSet.ready_replicas < statefulSet.total_replicas ? 'destructive' : 'default'}
                >
                  {statefulSet.ready_replicas}/{statefulSet.total_replicas}
                </Badge>
              </Table.Cell>
              <Table.Cell>{statefulSet.age}</Table.Cell>
              <Table.Cell>
                <DropdownMenu.Root>
                  <DropdownMenu.Trigger>
                    <Button class="h-auto p-2" variant="ghost">
                      <IconEllipsis />
                    </Button>
                  </DropdownMenu.Trigger>
                  <DropdownMenu.Content align="end">
                    <DropdownMenu.Group>
                      <DropdownMenu.Item
                        class="cursor-pointer"
                        onclick={() => {
                          k8sItem = statefulSet.name;
                          getDetails();
                        }}
                      >
                        View
                      </DropdownMenu.Item>
                      <DropdownMenu.Item class="cursor-pointer">Delete</DropdownMenu.Item>
                    </DropdownMenu.Group>
                  </DropdownMenu.Content>
                </DropdownMenu.Root>
              </Table.Cell>
            </Table.Row>
          {/each}
        {/if}
      </Table.Body>
    </Table.Root>
    <Pagination perPage={perPage} load={load} count={$statefulSets.length} />
  </div>

  {#if k8sItem}
    <Terminal
      type="stateful-set"
      getDetails={getDetails}
      updateItem={updateItem}
      k8sItem={k8sItem}
      details={details}
      bind:open={open}
    />
  {/if}
</div>
