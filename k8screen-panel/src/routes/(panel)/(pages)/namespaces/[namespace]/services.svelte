<script lang="ts">
  import Bar from '$lib/components/bar.svelte';
  import Terminal from '$lib/components/terminal.svelte';
  import * as Table from '$lib/components/ui/table';
  import { OptionTerminal } from '$lib/model/enum';
  import * as DropdownMenu from '$lib/components/ui/dropdown-menu';
  import type { Service } from '$lib/model/Service';
  import { serviceAPI } from '$lib/service/service-service';
  import * as yaml from 'yaml';
  import Button from '$lib/components/ui/button/button.svelte';
  import IconKebabMenu from '$lib/components/icons/IconKebabMenu.svelte';
  import Pagination from '$lib/components/pagination.svelte';

  export let namespace;

  let size: number = 5;

  let loading = true;
  let loadingTable = false;
  let option: OptionTerminal;
  let details: string;

  let services: Service[] = [];
  let paginated: Service[] = [];
  let k8sItem: string;
  let open: boolean;

  $: if (namespace) {
    getAllServices();
  }

  const load = (service: string) => {
    k8sItem = service;
    open = true;
    option = OptionTerminal.DETAIL;
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
  <div class="flex-grow flex flex-col gap-8 justify-between overflow-auto">
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
          {#each paginated as service}
            <Table.Row
              on:click={() => {
                load(service.name);
              }}
              class="cursor-pointer"
            >
              <Table.Cell>{service.name}</Table.Cell>
              <Table.Cell>{service.type}</Table.Cell>
              <Table.Cell>{service.cluster_ip}</Table.Cell>
              <Table.Cell>{service.external_ip}</Table.Cell>
              <Table.Cell>{service.ports}</Table.Cell>
              <Table.Cell>{service.age}</Table.Cell>
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
      <Pagination bind:pageSize={size} data={services} bind:paginated={paginated}/>
    </div>
  </div>

  <Terminal
    type="service"
    getDetails={getDetails}
    updateItem={updateItem}
    k8sItem={k8sItem}
    option={option}
    details={details}
    loading={loading}
    open={open}
  />
</div>
