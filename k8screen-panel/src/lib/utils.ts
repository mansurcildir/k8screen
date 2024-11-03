import { dev } from '$app/environment';
export const SPRING_BASE_URL = dev ? 'http://localhost:8080' : '';

import { type ClassValue, clsx } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
	return twMerge(clsx(inputs));
}

export interface Breadcrumb {
  text: string;
  link: string;
}

// Function to capitalize the first letter of a string
export function capitalizeFirstLetter(str: string): string {
  if (!str) return str; // Return the original string if it's empty
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

// Function to capitalize the first letter of each word in a sentence
export function capitalizeWords(sentence: string): string {
  return sentence
    .split(' ')
    .map((word) => capitalizeFirstLetter(word))
    .join(' ');
}


export function extractBreadcrumbs(pathname: string): Breadcrumb[] {
  if (pathname === '/' || pathname === '') {
    return [];
  }

  // Removing leading/trailing slashes and splitting
  const paths = pathname.replace(/^\/|\/$/g, '').split('/');
  let accumulatedPath = '';

  const breadcrumbs: Breadcrumb[] = paths.map((path) => {
    // Accumulate the path incrementally
    accumulatedPath += `/${path}`;

    // Filter out ULIDs for the text, but keep ULIDs for the link
    const text = isUlid(path)
      ? '' // If it's a ULID, keep the text empty
      : capitalizeWords(decodeURIComponent(path).replace(/[-_]+/g, ' '));

    return {
      text: text, // ULIDs won't affect the text, only non-ULID paths will
      link: accumulatedPath // Keep full path (including ULIDs) in the link
    };
  }).filter(crumb => crumb.text !== ''); // Filter out breadcrumbs with empty text

  return breadcrumbs;
}

export const isUlid = (id: string): boolean => {
  return /^[0-9A-HJKMNP-TV-Z]{26}$/.test(id.toUpperCase());
};