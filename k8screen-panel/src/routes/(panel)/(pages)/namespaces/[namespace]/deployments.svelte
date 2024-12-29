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

  const load = (deployment: string) => {
    k8sItem = deployment;
    open = true;
    option = OptionTerminal.DETAIL;
    getDetails();
  };

  const getAllDeployments = async () => {
    try {
      loadingTable = true;
      deployments = await deploymentAPI.getAllDeployments(namespace);
    } finally {
      loadingTable = false;
    }
  };

  const getDetails = async (): Promise<string> => {
    loading = true;
    details = await deploymentAPI.getDeploymentDetails(namespace, k8sItem);
    loading = false;
    return details;
  };

  const updateItem = async (editedDeployment: string) => {
    try {
      loading = true;
      return deploymentAPI.updateDeployment(namespace, k8sItem, yaml.parse(editedDeployment));
    } finally {
      loading = false;
    }
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
            <Table.Row on:click={() => load(deployment.name)} class="cursor-pointer">
              <Table.Cell>{deployment.name}</Table.Cell>
              <Table.Cell>{deployment.ready_replicas}/{deployment.total_replicas}</Table.Cell>
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
                      <DropdownMenu.Item onclick={() => (option = OptionTerminal.DETAIL)}>View</DropdownMenu.Item>
                      <DropdownMenu.Item onclick={() => (option = OptionTerminal.EDIT)}>Edit</DropdownMenu.Item>
                      <DropdownMenu.Item>Delete</DropdownMenu.Item>
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

  <Terminal
    type="deployment"
    getDetails={getDetails}
    updateItem={updateItem}
    k8sItem={k8sItem}
    option={option}
    details={details}
    loading={loading}
    open={open}
  />
</div>
