<script lang="ts">
  import Bar from '$lib/components/bar.svelte';
  import Terminal from '$lib/components/terminal.svelte';
  import * as Table from '$lib/components/ui/table';
  import type { Deployment } from '$lib/model/Deployment';
  import { deploymentAPI } from '$lib/service/deployment-service';
  import * as yaml from 'yaml';

  export let namespace;
  let loading = false;
  let loadingTable = false;
  let option: string = 'DETAILS';
  let details: string;

  let deployments: Deployment[] = [];
  let k8sItem: string;
  let open: boolean;

  $: if (namespace) {
    getAllDeployments();
  }

  const handleDeployment = (deployment: string) => {
    k8sItem = deployment;
    open = true;
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

<div class="flex flex-col" style="height: calc(100vh - 150px);">
  <div class="flex-grow overflow-auto">
    <Table.Root>
      <Table.Header>
        <Table.Row>
          <Table.Head>NAME</Table.Head>
          <Table.Head>REPLICAS</Table.Head>
          <Table.Head>UP-TO-DATE</Table.Head>
          <Table.Head>AVAILABLE</Table.Head>
          <Table.Head>AGE</Table.Head>
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
          </Table.Row>
        {:else}
          {#each deployments as deployment}
            <Table.Row on:click={() => handleDeployment(deployment.name)} class="cursor-pointer">
              <Table.Cell>{deployment.name}</Table.Cell>
              <Table.Cell>{deployment.ready_replicas}/{deployment.total_replicas}</Table.Cell>
              <Table.Cell>{deployment.up_to_date}</Table.Cell>
              <Table.Cell>{deployment.available}</Table.Cell>
              <Table.Cell>{deployment.age}</Table.Cell>
            </Table.Row>
          {/each}
        {/if}
      </Table.Body>
    </Table.Root>
  </div>

  <Terminal
    k8sItem={k8sItem}
    option={option}
    details={details}
    loading={loading}
    open={open}
    getDetails={getDetails}
    updateItem={updateItem}
  />
</div>
