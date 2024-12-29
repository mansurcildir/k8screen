<script lang="ts">
  import Bar from '$lib/components/bar.svelte';
  import Terminal from '$lib/components/terminal.svelte';
  import * as Table from '$lib/components/ui/table';
  import type { Service } from '$lib/model/Service';
  import { serviceAPI } from '$lib/service/service-service';
  import * as yaml from 'yaml';

  export let namespace;
  let loading = true;
  let loadingTable = false;
  let option: string = 'DETAILS';
  let details: string;

  let services: Service[] = [];
  let k8sItem: string;
  let open: boolean;

  $: if (namespace) {
    getAllServices();
  }

  const handleService = (service: string) => {
    k8sItem = service;
    open = true;
    getDetails();
  };

  const getAllServices = async () => {
    try {
      loadingTable = true;
      services = await serviceAPI.getAllServices(namespace);
    } finally {
      loadingTable = false;
    }
  };

  const getDetails = async (): Promise<string> => {
    loading = true;
    details = await serviceAPI.getServiceDetails(namespace, k8sItem);
    loading = false;
    return details;
  };

  const updateItem = async (editedService: string) => {
    try {
      loading = true;
      return serviceAPI.updateService(namespace, k8sItem, yaml.parse(editedService));
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
          <Table.Head>TYPE</Table.Head>
          <Table.Head>CLUSTER-IP</Table.Head>
          <Table.Head>EXTERNAL-IP</Table.Head>
          <Table.Head>PORTS</Table.Head>
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
          {#each services as service}
            <Table.Row
              on:click={() => {
                handleService(service.name);
              }}
              class="cursor-pointer"
            >
              <Table.Cell>{service.name}</Table.Cell>
              <Table.Cell>{service.type}</Table.Cell>
              <Table.Cell>{service.cluster_ip}</Table.Cell>
              <Table.Cell>{service.external_ip}</Table.Cell>
              <Table.Cell>{service.ports}</Table.Cell>
              <Table.Cell>{service.age}</Table.Cell>
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
