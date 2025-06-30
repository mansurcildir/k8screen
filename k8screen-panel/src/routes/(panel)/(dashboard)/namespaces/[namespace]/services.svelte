<script lang="ts">
  import Bar from '$lib/components/bar.svelte';
  import Terminal from '$lib/components/terminal.svelte';
  import * as Table from '$lib/components/ui/table';
  import * as DropdownMenu from '$lib/components/ui/dropdown-menu';
  import type { Service } from '$lib/model/Service';
  import { serviceAPI } from '$lib/service/service-service';
  import * as yaml from 'yaml';
  import Button from '$lib/components/ui/button/button.svelte';
  import IconEllipsis from 'lucide-svelte/icons/ellipsis';
  import Pagination from '$lib/components/pagination.svelte';
  import { services, getAllServices, loadingService } from '$lib/store';

  export let namespace;

  let perPage: number = 5;
  let details: string;
  let paginated: Service[] = [];
  let k8sItem: string;
  let open: boolean;

  $: if (namespace) {
    getAllServices(namespace).then(() => load(1));
  }

  const load = (page: number) => {
    const startIndex = (page - 1) * perPage;
    const endIndex = page * perPage;
    paginated = $services.slice(startIndex, endIndex);
  };

  const getDetails = async (): Promise<string> => {
    open = true;
    details = await serviceAPI.getServiceDetails(namespace, k8sItem);
    return details;
  };

  const updateItem = async () => {
    details = await serviceAPI.updateService(namespace, k8sItem, yaml.parse(k8sItem));
    return details;
  };
</script>

<div class="flex flex-col" style="height: calc(100vh - 150px);">
  <div class="flex flex-grow flex-col justify-between gap-8 overflow-auto">
    <Table.Root>
      <Table.Header>
        <Table.Row>
          <Table.Head>NAME</Table.Head>
          <Table.Head>TYPE</Table.Head>
          <Table.Head>CLUSTER-IP</Table.Head>
          <Table.Head>EXTERNAL-IP</Table.Head>
          <Table.Head>PORTS</Table.Head>
          <Table.Head>AGE</Table.Head>
          <Table.Head>OPTIONS</Table.Head>
        </Table.Row>
      </Table.Header>
      <Table.Body>
        {#if $loadingService}
          <Table.Row>
            <Table.Cell><Bar /></Table.Cell>
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
                k8sItem = service.name;
                open = true;
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
                    <Button class="h-auto p-2" variant="ghost">
                      <IconEllipsis />
                    </Button>
                  </DropdownMenu.Trigger>
                  <DropdownMenu.Content align="end">
                    <DropdownMenu.Group>
                      <DropdownMenu.Item
                        class="cursor-pointer"
                        onclick={() => {
                          k8sItem = service.name;
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
    <Pagination perPage={perPage} load={load} count={$services.length} />
  </div>

  {#if k8sItem}
    <Terminal
      type="service"
      getDetails={getDetails}
      updateItem={updateItem}
      k8sItem={k8sItem}
      details={details}
      bind:open={open}
    />
  {/if}
</div>
