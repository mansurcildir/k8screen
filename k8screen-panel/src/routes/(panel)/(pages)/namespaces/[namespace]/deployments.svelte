<script lang="ts">
  import Bar from '$lib/components/bar.svelte';
  import Terminal from '$lib/components/terminal.svelte';
  import * as Table from '$lib/components/ui/table';
  import type { Deployment } from '$lib/model/Deployment';
  import { OptionTerminal } from '$lib/model/enum';
  import * as DropdownMenu from '$lib/components/ui/dropdown-menu';
  import { deploymentAPI } from '$lib/service/deployment-service';
  import yaml from 'yaml';
  import Button from '$lib/components/ui/button/button.svelte';
  import IconKebabMenu from '$lib/components/icons/IconKebabMenu.svelte';
  import Pagination from '$lib/components/pagination.svelte';
  import Badge from '$lib/components/ui/badge/badge.svelte';

  export let namespace;

  let size: number = 5;

  let loading = false;
  let loadingTable = false;
  let option: OptionTerminal;
  let details: string;

  let deployments: Deployment[] = [];
  let paginated: Deployment[] = [];
  let k8sItem: string;
  let open: boolean;

  $: if (namespace) {
    getAllDeployments();
  }

  const getAllDeployments = async () => {
    loadingTable = true;
    deployments = await deploymentAPI.getAllDeployments(namespace);
    loadingTable = false;
  };

  const getDetails = async (deployment: string, opt: OptionTerminal): Promise<string> => {
    loading = true;
    open = true;
    k8sItem = deployment;
    option = opt;
    details = await deploymentAPI.getDeploymentDetails(namespace, deployment);
    loading = false;
    return details;
  };

  const updateItem = async (deployment: string) => {
    loading = true;
    details = await deploymentAPI.updateDeployment(namespace, k8sItem, yaml.parse(deployment));
    loading = false;
    return details;
  };
</script>

<div class="flex flex-col justify-between" style="height: calc(100vh - 150px);">
  <div class="flex-grow flex flex-col gap-8 justify-between overflow-auto">
    <Table.Root>
      <Table.Header>
        <Table.Row>
          <Table.Head>NAME</Table.Head>
          <Table.Head>REPLICAS</Table.Head>
          <Table.Head>UP-TO-DATE</Table.Head>
          <Table.Head>AVAILABLE</Table.Head>
          <Table.Head>AGE</Table.Head>
          <Table.Head></Table.Head>
        </Table.Row>
      </Table.Header>
      <Table.Body>
        {#if loadingTable}
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
            <Table.Row on:click={() => getDetails(deployment.name, OptionTerminal.DETAIL)} class="cursor-pointer">
              <Table.Cell>{deployment.name}</Table.Cell>
              <Table.Cell>
                <Badge
                  class="w-20 flex justify-center"
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
                    <Button class="p-2 h-auto" variant="ghost">
                      <IconKebabMenu />
                    </Button>
                  </DropdownMenu.Trigger>
                  <DropdownMenu.Content align="end">
                    <DropdownMenu.Group>
                      <DropdownMenu.Group>
                        <DropdownMenu.Item
                          onclick={() => {
                            getDetails(deployment.name, OptionTerminal.DETAIL);
                          }}>View</DropdownMenu.Item
                        >
                        <DropdownMenu.Item
                          onclick={() => {
                            getDetails(deployment.name, OptionTerminal.EDIT);
                          }}>Edit</DropdownMenu.Item
                        >
                        <DropdownMenu.Item>Delete</DropdownMenu.Item>
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
    <div class="mb-5">
      <Pagination bind:pageSize={size} data={deployments} bind:paginated={paginated} />
    </div>
  </div>

  {#if k8sItem}
    <Terminal
      type="deployment"
      getDetails={getDetails}
      updateItem={updateItem}
      k8sItem={k8sItem}
      option={option}
      details={details}
      loading={loading}
      bind:open={open}
    />
  {/if}
</div>
