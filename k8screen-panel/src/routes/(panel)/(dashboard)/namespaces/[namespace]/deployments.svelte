<script lang="ts">
  import Bar from '$lib/components/bar.svelte';
  import Terminal from '$lib/components/terminal.svelte';
  import * as Table from '$lib/components/ui/table';
  import type { Deployment } from '$lib/model/Deployment';
  import * as DropdownMenu from '$lib/components/ui/dropdown-menu';
  import { deploymentAPI } from '$lib/service/deployment-service';
  import yaml from 'yaml';
  import Button from '$lib/components/ui/button/button.svelte';
  import Pagination from '$lib/components/pagination.svelte';
  import Badge from '$lib/components/ui/badge/badge.svelte';
  import { deployments, getAllDeployments, loadingDeployment } from '$lib/store';
  import IconEllipsis from 'lucide-svelte/icons/ellipsis';
  import { toastService } from '$lib/service/toast-service';

  export let namespace: string;

  let perPage: number = 5;
  let details: string;
  let paginated: Deployment[] = [];
  let k8sItem: string;
  let open: boolean;

  $: if (namespace) {
    loadDeployments(namespace);
  }

  const loadDeployments = async (namespace: string) => {
    getAllDeployments(namespace)
    .then(() =>  load(1))
    .catch((err) => {
      toastService.show(err.message, 'error');
    });
  };

  const load = (page: number) => {
    const startIndex = (page - 1) * perPage;
    const endIndex = page * perPage;
    paginated = $deployments.slice(startIndex, endIndex);
  };

  const getDetails = async (): Promise<string> => {
    open = true;
    return deploymentAPI.getDeploymentDetails(namespace, k8sItem)
    .then((res) => {
      details = res.data;
      return details;
    })
  };

  const updateItem = async () => {
    return deploymentAPI.updateDeployment(namespace, k8sItem, yaml.parse(k8sItem))
    .then((res) => {
      details = res.data;
      return details;
    })
  };
</script>

<div class="flex flex-col justify-between" style="height: calc(100vh - 150px);">
  <div class="flex flex-grow flex-col justify-between gap-8 overflow-auto">
    <Table.Root>
      <Table.Header>
        <Table.Row>
          <Table.Head>NAME</Table.Head>
          <Table.Head>REPLICAS</Table.Head>
          <Table.Head>UP-TO-DATE</Table.Head>
          <Table.Head>AVAILABLE</Table.Head>
          <Table.Head>AGE</Table.Head>
          <Table.Head>OPTIONS</Table.Head>
        </Table.Row>
      </Table.Header>
      <Table.Body>
        {#if $loadingDeployment}
          <Table.Row>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
          </Table.Row>
        {:else}
          {#each paginated as deployment}
            <Table.Row
              on:click={() => {
                k8sItem = deployment.name;
                open = true;
              }}
              class="cursor-pointer"
            >
              <Table.Cell>{deployment.name}</Table.Cell>
              <Table.Cell>
                <Badge
                  class="flex w-20 justify-center"
                  variant={deployment.ready_replicas < deployment.total_replicas ? 'destructive' : 'default'}
                >
                  {deployment.ready_replicas}/{deployment.total_replicas}
                </Badge>
              </Table.Cell>
              <Table.Cell>{deployment.up_to_date}</Table.Cell>
              <Table.Cell>{deployment.available}</Table.Cell>
              <Table.Cell>{deployment.age}</Table.Cell>
              <Table.Cell>
                <DropdownMenu.Root>
                  <DropdownMenu.Trigger>
                    <Button class="h-auto p-2" variant="ghost">
                      <IconEllipsis />
                    </Button>
                  </DropdownMenu.Trigger>
                  <DropdownMenu.Content align="end">
                    <DropdownMenu.Group>
                      <DropdownMenu.Group>
                        <DropdownMenu.Item
                          class="cursor-pointer"
                          onclick={() => {
                            k8sItem = deployment.name;
                            getDetails();
                          }}>View</DropdownMenu.Item
                        >
                        <DropdownMenu.Item class="cursor-pointer">Delete</DropdownMenu.Item>
                      </DropdownMenu.Group>
                    </DropdownMenu.Group>
                  </DropdownMenu.Content>
                </DropdownMenu.Root>
              </Table.Cell>
            </Table.Row>
          {/each}
        {/if}
      </Table.Body>
    </Table.Root>
    <Pagination perPage={perPage} load={load} count={$deployments.length} />
  </div>

  {#if k8sItem}
    <Terminal
      type="deployment"
      getDetails={getDetails}
      updateItem={updateItem}
      k8sItem={k8sItem}
      details={details}
      bind:open={open}
    />
  {/if}
</div>
